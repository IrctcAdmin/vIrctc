package com.vmware.irctc.utils;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JpaDatabaseService implements DatabaseService {
    public static final String UPDATEDBY_METHOD_NAME = "setUpdatedBy";
    protected final Logger LOGGER = Logger.getLogger(getClass());

    @PersistenceContext
    private EntityManager em;

    @Override
    public EntityManager getEntityManagerInstance() {
        return em;
    }

    @Transactional(readOnly = false)
    @Override
    public <T> T create(T object) {
        em.persist(object);
        return object;
    }

    @Override
    public <T> T createNoTransaction(T object) {
        em.persist(object);
        return object;
    }

    @Transactional(readOnly = false)
    @Override
    public <T> T update(T object) {
        object = em.merge(object);
        return object;
    }

    @Override
    public <T> T updateNoTransaction(T object) {
        object = em.merge(object);
        return object;
    }

    @Transactional(readOnly = false)
    @Override
    public void delete(Object object) {
        if (object != null)
            em.remove(object);
    }

    @Override
    public void deleteNoTransaction(Object object) {
        if (object != null)
            em.remove(object);
    }

    @Transactional(readOnly = false)
    @Override
    public <T> void deleteById(Object id, Class<T> c) {
        T object = findById(id, c);
        if (object != null)
            delete(object);
    }

    @Transactional(readOnly = false)
    @Override
    public <T> void deleteById(Object id, Class<T> c, String userName, String methodName) {
        T object = findById(id, c);
        if (object != null) {
            try {
                Method idMethod = object.getClass().getMethod(methodName, new Class[]{String.class});
                if (idMethod != null) {
                    idMethod.invoke(object, new Object[] { userName });
                    em.merge(object);
                    em.flush();
                }
            }catch(Exception e) {
                LOGGER.warn("cannot find setter method for updatedBy property in " + object.getClass().getName());
            }
            em.remove(object);
        }
    }

    @Transactional(readOnly = false)
    @Override
    public <T> void deleteObsolete(List<T> newObjects, List<T> oldObjects, String userName, String methodName) {
        if (oldObjects == null)
            return;

        // make a copy of old list
        List<T> oldObjectsCopy = new ArrayList<T>();
        oldObjectsCopy.addAll(oldObjects);

        // remove new instances from the old list
        for (int i = 0; i < newObjects.size(); i++) {
            T obj = newObjects.get(i);
            oldObjectsCopy.remove(obj);
        }

        // delete remaining objects in the old list
        for (int i = 0; i < oldObjectsCopy.size(); i++) {
            T obj = oldObjectsCopy.get(i);
            if (obj != null) {
                try {
                    Method idMethod = obj.getClass().getMethod(methodName, new Class[]{String.class});
                    if (idMethod != null) {
                        idMethod.invoke(obj, new Object[] { userName });
                        em.merge(obj);
                        em.flush();
                    }
                }catch(Exception e) {
                    LOGGER.warn("cannot find the method for updatedBy property in " + obj.getClass().getName());
                }
                em.remove(obj);
            }
        }
    }

    @Override
    public <T> long count(Class<T> c) {
        String format = "select count(o) from %s o";
        String queryStr = String.format(format, c.getCanonicalName());
        TypedQuery<Long> query = em.createQuery(queryStr, Long.class);
        return query.getSingleResult();
    }

    @Override
    public <T> T findById(Object id, Class<T> c) {
        if (id == null)
            return null;
        return em.find(c, id);
    }

    @Override
    public <T> T findExisting(Integer id, List<T> list, Class<T> c) {
        if (id == null || list == null)
            return null;

        // check list for object with ID equal to requested ID parameter
        for (int i = 0; i < list.size(); i++) {
            T obj = list.get(i);

            Object idValue = getIdValue(obj);
            if (idValue != null && idValue.equals(id))
                return obj;
        }

        return null;
    }

    @Override
    public Object getIdValue(Object obj) {
        PersistenceUnitUtil persistenceUnitUtil = em.getEntityManagerFactory().getPersistenceUnitUtil();
        Object idValue = persistenceUnitUtil.getIdentifier(obj);
        return idValue;
    }

    @Override
    public void setIdValue(Object obj, Object value) throws Exception {
        // check object methods for @Id annotation
        Method idMethod = null;
        Method[] methods = obj.getClass().getMethods();
        for (int j = 0; idMethod == null && j < methods.length; j++) {
            Method method = methods[j];
            Annotation[] annotations = method.getAnnotations();
            for (int k = 0; k < annotations.length; k++) {
                Annotation annotation = annotations[k];
                if (annotation.annotationType().equals(Id.class)) {
                    idMethod = method;
                    break;
                }
            }
        }

        // assume integer ID class if value is null
        Class<? extends Object> valueClass = value != null ? value.getClass() : Integer.class;

        // check if found Id method
        if (idMethod != null) {
            // if getter method then find to setter
            if (idMethod.getName().startsWith("get")) {
                String setMethodName = "set" + idMethod.getName().substring(3);
                idMethod = obj.getClass().getMethod(setMethodName, new Class[]{valueClass});
            }

            if (idMethod == null)
                throw new IllegalArgumentException("cannot find setter method for @Id property in " + obj.getClass().getName());

            idMethod.invoke(obj, new Object[] { value });
            return;
        }

        // check object fields for @Id annotation, @Id field is usually first so find should be fast
        Field[] fields = obj.getClass().getDeclaredFields();
        Field idField = null;
        for (int j = 0; idField == null && j < fields.length; j++) {
            Field field = fields[j];
            Annotation[] annotations = field.getAnnotations();
            for (int k = 0; k < annotations.length; k++) {
                Annotation annotation = annotations[k];
                if (annotation.annotationType().equals(Id.class)) {
                    idField = field;
                    break;
                }
            }
        }

        // check if found Id field, and check value
        if (idField == null)
            throw new IllegalArgumentException("cannot find @Id method/filed in " + obj.getClass().getName());

        // field may be private, use get method to read value
        if (!idField.isAccessible())
            idField.setAccessible(true);

        // set value
        idField.set(obj, value);
    }

    @Override
    public <T> List<T> findAll(Class<T> c) {
        String format = "select o from %s o";
        String queryStr = String.format(format, c.getSimpleName());
        TypedQuery<T> query = em.createQuery(queryStr, c);
        enableQueryCaching(query);
        return query.getResultList();
    }

    /** set flag to enable query caching */
    protected void enableQueryCaching(Query query) {
        if (query instanceof org.hibernate.Query) {
            ((org.hibernate.Query)query).setCacheable(true);
        }
    }

    @Override
    public <T> List<T> findAll(Class<T> c, String condition) throws Exception {
        String format = "select o from %s o ";
        String queryStr = String.format(format, c.getSimpleName());
        if (condition != null) {
            queryStr += condition;
        }
        TypedQuery<T> query = em.createQuery(queryStr, c);
        enableQueryCaching(query);
        return query.getResultList();
    }

    @Override
    public <T> List<T> findAll(Class<T> c, int firstResult, int maxResults) {
        String format = "select o from %s o";
        String queryStr = String.format(format, c.getCanonicalName());
        TypedQuery<T> query = em.createQuery(queryStr, c);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        enableQueryCaching(query);
        return query.getResultList();
    }

    @Override
    public <T> long count(Class<T> c, String condition) throws Exception {
        String format = "select count(o) from %s o ";
        String queryStr = String.format(format, c.getSimpleName());

        if (condition != null) {
            queryStr += condition;
        }

        TypedQuery<Long> query = em.createQuery(queryStr, Long.class);
        return query.getSingleResult();
    }

    @Override
    public <T> List<T> findAll(Class<T> c, Object ... args) {
        String format = "select o from %s o";
        String queryStr = String.format(format, c.getSimpleName());

        // append sort criteria
        queryStr += " order by";
        for (int i = 0; i < args.length; i += 2) {
            String field = (String)args[i];
            Boolean ascending = (Boolean)args[i+1];
            String condition = (i > 0 ? "," : "") + " o." + field + (ascending ? " asc" : " desc");
            queryStr += condition;
        }

        TypedQuery<T> query = em.createQuery(queryStr, c);
        enableQueryCaching(query);
        return query.getResultList();
    }

    private Object callGetMethod(Object object, String methodName) {
        if (object != null) {
            Method[] methods = object.getClass().getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (method.getName().equalsIgnoreCase(methodName)) {
                    try {
                        Object value = method.invoke(object, new Object[0]);
                        return value;
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }
        return null;
    }

    private Object getLastUpdate(Object object) {
        Object date = callGetMethod(object, "getLastUpdate");
        return clearMillisecond(date);
    }

    private Object getUpdatedBy(Object object) {
        return callGetMethod(object, "getUpdatedBy");
    }

    @Override
    public void checkConcurrentModification(Object oldObject, Object newObject)
            throws Exception {
        if (oldObject != null && newObject != null) {
            Object oldDate = getLastUpdate(oldObject);
            Object newDate = getLastUpdate(newObject);
            Object oldUpdatedBy = getUpdatedBy(oldObject);
            Object newUpdatedBy = getUpdatedBy(newObject);
            boolean sameUser = oldUpdatedBy != null && newUpdatedBy != null && oldUpdatedBy.equals(newUpdatedBy);
            if (!sameUser && oldDate != null && newDate != null && !oldDate.equals(newDate)) {
                String name = newObject.getClass().getSimpleName();
                throw new Exception("Unable to update " + name +
                        " because it was modified at " + newDate.toString() +
                        (newUpdatedBy == null ? "" : " by " + newUpdatedBy) +
                        " while your were trying to update it.");
            }
        }

    }

    private Object clearMillisecond(Object date) {
        if (date instanceof Date) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime((Date) date);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar.getTime();
        }
        return date;
    }

    /** Return property name with @Id attribute */
    public String getIdPropertyName(Class<? extends Object> class1) {
        EntityType<? extends Object> entity = em.getEntityManagerFactory().getMetamodel().entity(class1);
        SingularAttribute<?, ? extends Object> id = entity.getId(Integer.class);
        String idProperty = id.getName();
        return idProperty;
    }

    /** Flush all pending data in current entity manager */
    @Override
    public void flushAll() {
        em.flush();
    }

    /** converts list of integer ids to string */
    protected String listToIdString(List<Integer> ids) {
        String idsStr = "(" + StringUtils.join(ids, ", ") + ")";
        return idsStr;
    }

}
