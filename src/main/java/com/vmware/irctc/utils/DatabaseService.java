package com.vmware.irctc.utils;


import java.util.List;

import javax.persistence.EntityManager;

/** Common methods to work with JPA to find/create/delete objects */
public interface DatabaseService {

    public EntityManager getEntityManagerInstance();

    /** Create a new object in DB */
	public <T> T create(T object);

    /** Create a new object in DB */
    public <T> T createNoTransaction(T object);

    /** Update entity object in DB */
    public <T> T update(T obj);

    /** Delete object from DB */
    public void delete(Object object);

    /** Update object in the current JPA transaction */
    public <T> T updateNoTransaction(T object);

    /** Delete object from DB in the current JPA transaction */
    public void deleteNoTransaction(Object object);

    /** Create object from DB by ID */
    public <T> void deleteById(Object id, Class<T> c);

    /** Create object from DB by ID */
    public <T> void deleteById(Object id, Class<T> c, String userName, String methodName) throws Exception;

    /** Delete objects which are present in the old list but not in new list */
    public <T> void deleteObsolete(List<T> newObjects, List<T> oldObjects, String userName, String methodName);

    /** Find object by ID (some tables use non-int ids) */
    public <T> T findById(Object id, Class<T> c);

    /** Find object by int ID in the list of existing objects */
    public <T> T findExisting(Integer id, List<T> list, Class<T> c);

    /** Get ID value of the object */
    public Object getIdValue(Object obj);

    /** Set ID value of the object */
    public void setIdValue(Object obj, Object value) throws Exception;

    /** Count all objects */
    public <T> long count(Class<T> c);

    /**
     * Get all objects.
     * this method return sorted data using EwebOrderBy attributes defined in entity class on properties and fields
     */
    public <T> List<T> findAll(Class<T> c);

    /**
     * Get all objects
     * @param <T>
     * @param c
     * @param condition: can be anthing like sorting order, and/or filter, and/or group by
     * @return
     */
    public <T> List<T> findAll(Class<T> c, String condition) throws Exception;

    /** Count objects with condition */
    public <T> long count(Class<T> c, String condition) throws Exception;

    /**
     * Get all objects.
     * this method return sorted data using EwebOrderBy attributes defined in entity class on properties and fields
     */
    public <T> List<T> findAll(Class<T> c, int firstResult, int maxResults);

    /**
     * Get all objects sorted by one or more fields
     *
     * @param c     entity class
     * @param       args    one or more pairs of String field name and Boolean ascending parameter
     * @return      sorted result
     */
    public <T> List<T> findAll(Class<T> c, Object ... args);

    public void checkConcurrentModification(Object oldObject, Object newObject) throws Exception;
    /** Flush all pending data in current entity manager */
    public void flushAll();
}
