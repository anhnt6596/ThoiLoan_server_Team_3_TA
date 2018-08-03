package util.database;

import bitzero.util.common.business.Debug;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.spy.memcached.CASResponse;
import net.spy.memcached.CASValue;

import org.json.JSONObject;

import util.server.ServerUtil;

public class DataModel {
    static final Gson gson = new Gson();
    //static final long serialVersionUID = 6802410421380852275L;

    private static final byte TYPE_DATABASEOBJECT = 0;
    private static final byte TYPE_ARRAYLIST = 1;
    private static final byte TYPE_HASHMAP = 2;
    private static final byte TYPE_ARRAY_DATABASEOBJECT = 3;
    private static final byte TYPE_PRIMITIVE = Byte.MAX_VALUE;

    private static final Map<String, List<Field>> cachedMapField = new HashMap<String, List<Field>>();

    public DataModel() {
        super();
    }

    public void saveModel(String key) throws Exception {
        String globalKey = ServerUtil.getModelKeyName(this.getClass().getSimpleName(), key);
        String sobj = gson.toJson(this);
        DataHandler.set(globalKey, sobj);
    }

    public void saveModel(int uId) throws Exception {
        String key = ServerUtil.getModelKeyName(this.getClass().getSimpleName(), uId);
        String sobj = gson.toJson(this);
        DataHandler.set(key, sobj);
        //DataHandler.set(key, this);
    }

    public void saveModel(long uId) throws Exception {
        String key = ServerUtil.getModelKeyName(this.getClass().getSimpleName(), uId);
        String sobj = gson.toJson(this);
        DataHandler.set(key, sobj);
        //DataHandler.set(key, this);
    }

    public static Object getModel(String key, Class c) throws Exception {
        String globalKey = ServerUtil.getModelKeyName(c.getSimpleName(), key);
        return gson.fromJson((String) DataHandler.get(globalKey), c);
        //return DataHandler.get(key);
    }

    public static Object getModel(int uId, Class c) throws Exception {
        String key = ServerUtil.getModelKeyName(c.getSimpleName(), uId);
        return gson.fromJson((String) DataHandler.get(key), c);
        //return DataHandler.get(key);
    }

    public static Object getModel(long uId, Class c) throws Exception {
        String key = ServerUtil.getModelKeyName(c.getSimpleName(), uId);
        return gson.fromJson((String) DataHandler.get(key), c);
        //return DataHandler.get(key);
    }

    public static Object getSocialModel(long uId, Class c) throws Exception {
        String key = ServerUtil.getSocialModelKeyName(c.getSimpleName(), uId);
        return DataHandler.get(key);
    }

    public void saveSocialModel(long uId) throws Exception {
        String key = ServerUtil.getSocialModelKeyName(this.getClass().getSimpleName(), uId);
        DataHandler.set(key, this);
    }

    public static CASValue getS(int uId, Class c) throws Exception {
        String key = ServerUtil.getModelKeyName(c.getSimpleName(), uId);
        return DataHandler.getS(key);
    }

    public boolean checkAndSet(int uId, long valCAS) throws Exception {
        String key = ServerUtil.getModelKeyName(this.getClass().getSimpleName(), uId);
        CASResponse casRes = DataHandler.checkAndSet(key, valCAS, this);
        if (casRes.equals(CASResponse.OK))
            return true;
        else
            return false;
    }

    /*********************************************************
     *                                                       *
     * methods below used for display data in Admin Tool only*
     *                                                       *
     *********************************************************/
    public String toJSON() {
        try {
            return new JSONObject(reflectObjectToMap(this)).toString();
        } catch (Exception e) {
        }
        return null;
    }

    public static final HashMap<String, Object> reflectObjectToMap(DataModel pi) throws Exception {
        if (pi == null)
            return null;
        Type type;
        HashMap<String, Object> properties = new HashMap<String, Object>();
        List<Field> fields = getFieldInfos(pi);
        for (int i = 0; i < fields.size(); i++) {
            Field f = fields.get(i);
            Object value;
            try {
                value = f.get(pi);
                byte typeCode = getTypeCode(value);
                switch (typeCode) {
                case TYPE_DATABASEOBJECT:
                    properties.put(f.getName(), DataModel.reflectObjectToMap((DataModel) value));
                    break;
                case TYPE_ARRAY_DATABASEOBJECT:
                    DataModel[] arrDes = (DataModel[]) value;
                    if (arrDes.length == 0)
                        continue;
                    Map[] arrMap = new Map[arrDes.length];
                    for (int k = 0; k < arrDes.length; k++) {
                        if (arrDes[k] != null)
                            arrMap[k] = DataModel.reflectObjectToMap(arrDes[k]);
                    }
                    properties.put(f.getName(), arrMap);
                    break;
                case TYPE_ARRAYLIST:
                    // lay ra kieu du lieu cua field dang duyet
                    type = f.getGenericType();
                    if (type instanceof ParameterizedType) {
                        // field la cau truc co nhieu kieu du lieu
                        ParameterizedType pType = (ParameterizedType) type;
                        // lay ra kieu du lieu component
                        Type keyType = pType.getActualTypeArguments()[0];
                        // lay class cua component
                        String classNameComponent = keyType.toString().split(" ")[1];
                        Class component = Class.forName(classNameComponent);
                        if (isNumeric(component) || isString(component)) {
                            properties.put(f.getName(), value);
                        } else {
                            Object oPattern = component.newInstance();
                            if (oPattern instanceof
                                DataModel) {
                                // neu la databaseObject
                                ArrayList<HashMap<String, Object>> map =
                reflectArrayObjectToArrayMap((ArrayList) value);
                                properties.put(f.getName(), map);
                            } else {
                                throw new Exception("unsupport field " + f.getName() + "-" + component);
                            }
                        }

                    }


                    break;
                case TYPE_HASHMAP:
                    type = f.getGenericType();
                    if (type instanceof ParameterizedType) {
                        // field la cau truc co nhieu kieu du lieu
                        ParameterizedType pType = (ParameterizedType) type;
                        // lay ra kieu du lieu cua key
                        Type keyType = pType.getActualTypeArguments()[0];
                        String classNameKeyType = keyType.toString().split(" ")[1];
                        Class classKey = Class.forName(classNameKeyType);

                        // lay ra kieu du lieu cua value
                        Type valueType = pType.getActualTypeArguments()[1];
                        String classValueType = valueType.toString().split(" ")[1];
                        Class classValue = Class.forName(classValueType);
                        Map map = reflectHashMapObjectToHashMapProperties((HashMap) value, classKey, classValue);
                        properties.put(f.getName(), map);
                    }

                    break;
                default:
                    properties.put(f.getName(), value);
                    break;
                }
            } catch (Exception e) {
                //Debug.trace(e.getStackTrace());
                Debug.trace(e.getStackTrace().toString());
                throw new Exception(e.getMessage(), e.getCause());
            }

        }
        return properties;
    }

    private static final byte getTypeCode(Object o) {
        if (o instanceof DataModel)
            return TYPE_DATABASEOBJECT;
        if (o instanceof ArrayList)
            return TYPE_ARRAYLIST;
        if (o instanceof HashMap)
            return TYPE_HASHMAP;
        if (o instanceof DataModel[])
            return TYPE_ARRAY_DATABASEOBJECT;
        return TYPE_PRIMITIVE;
    }

    private static final List<Field> getFieldInfos(DataModel pi) {
        String key = pi.getClass().getSimpleName();
        List<Field> fieldInfos = cachedMapField.get(key);
        if (fieldInfos == null) {
            Field myFields[] = pi.getClass().getDeclaredFields();
            Field parentFields[] = new Field[0];
            Field grandFields[] = new Field[0];
            Field grandGrandFields[] = new Field[0];

            try {
                parentFields = pi.getClass().getSuperclass().getDeclaredFields();
            } catch (Exception e) {
                parentFields = new Field[0];
            }
            try {
                grandFields = pi.getClass().getSuperclass().getSuperclass().getDeclaredFields();
            } catch (Exception e) {
                grandFields = new Field[0];
            }
            try {
                grandGrandFields = pi.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredFields();

            } catch (Exception e) {
                grandGrandFields = new Field[0];
            }

            fieldInfos = new ArrayList<Field>();
            for (int i = 0; i < myFields.length; i++) {
                Field f = myFields[i];
                if ((f.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
                    continue;
                } else {
                    fieldInfos.add(f);
                }
            }
            if (parentFields.length > 0) {
                for (int i = 0; i < parentFields.length; i++) {
                    Field f = parentFields[i];
                    if ((f.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
                        continue;
                    } else {
                        fieldInfos.add(f);
                    }
                }
            }

            if (grandFields.length > 0) {
                for (int i = 0; i < grandFields.length; i++) {
                    Field f = grandFields[i];
                    if ((f.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
                        continue;
                    } else {
                        fieldInfos.add(f);
                    }
                }
            }

            if (grandGrandFields.length > 0) {
                for (int i = 0; i < grandGrandFields.length; i++) {
                    Field f = grandGrandFields[i];
                    if ((f.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
                        continue;
                    } else {
                        fieldInfos.add(f);
                    }
                }
            }

            cachedMapField.put(key, fieldInfos);
        }

        return fieldInfos;
    }

    private static final boolean isNumeric(Class obj) {
        return (obj == Integer.class || obj == Float.class || obj == Short.class || obj == Long.class ||
                obj == Double.class);
    }

    private static final boolean isString(Class obj) {
        return (obj == String.class);
    }

    public static final ArrayList<HashMap<String, Object>> reflectArrayObjectToArrayMap(ArrayList listDatabaseObject) throws Exception {
        ArrayList<HashMap<String, Object>> retVal = new ArrayList<HashMap<String, Object>>();
        if (listDatabaseObject.size() == 0)
            return retVal;
        for (int i = 0; i < listDatabaseObject.size(); i++) {
            retVal.add(reflectObjectToMap((DataModel) listDatabaseObject.get(i)));
        }
        return retVal;
    }

    public static final HashMap<Object, Object> reflectHashMapObjectToHashMapProperties(HashMap srcMap, Class classKey,
                                                                                        Class classValue) throws Exception,
                                                                                                                 NoSuchMethodException,
                                                                                                                 InstantiationException,
                                                                                                                 IllegalAccessException,
                                                                                                                 InvocationTargetException {
        HashMap<Object, Object> retVal = null;
        if (!isNumeric(classKey) && !isString(classKey)) {
            throw new Exception("Not support datatype of key not is numeric or String " + classKey);
        }
        Object valuePattern = null;
        if (isNumeric(classValue) || isString(classValue)) {
            return srcMap;
        } else {
            if (classValue.isArray() || classValue.isMemberClass()) {
                throw new Exception("Not support datatype of value is array or membaseClasee " + classValue);
            }
            valuePattern = classValue.newInstance();
            if (valuePattern instanceof DataModel) {
                retVal = new HashMap<Object, Object>();
                Iterator<Map.Entry> it = srcMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = it.next();
                    retVal.put(entry.getKey(), reflectObjectToMap((DataModel) entry.getValue()));
                }
                return retVal;
            } else {
                throw new Exception("Not support datatype of value " + classValue);
            }
        }
    }
}
