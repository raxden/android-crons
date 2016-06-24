package io.realm;


import android.util.JsonReader;
import android.util.JsonToken;
import com.raxdenstudios.cron.model.Cron;
import io.realm.RealmFieldType;
import io.realm.exceptions.RealmMigrationNeededException;
import io.realm.internal.ColumnInfo;
import io.realm.internal.ImplicitTransaction;
import io.realm.internal.LinkView;
import io.realm.internal.RealmObjectProxy;
import io.realm.internal.Row;
import io.realm.internal.Table;
import io.realm.internal.TableOrView;
import io.realm.internal.android.JsonUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CronRealmProxy extends Cron
    implements RealmObjectProxy, CronRealmProxyInterface {

    static final class CronColumnInfo extends ColumnInfo {

        public final long idIndex;
        public final long typeIndex;
        public final long triggerAtTimeIndex;
        public final long intervalIndex;
        public final long statusIndex;

        CronColumnInfo(String path, Table table) {
            final Map<String, Long> indicesMap = new HashMap<String, Long>(5);
            this.idIndex = getValidColumnIndex(path, table, "Cron", "id");
            indicesMap.put("id", this.idIndex);

            this.typeIndex = getValidColumnIndex(path, table, "Cron", "type");
            indicesMap.put("type", this.typeIndex);

            this.triggerAtTimeIndex = getValidColumnIndex(path, table, "Cron", "triggerAtTime");
            indicesMap.put("triggerAtTime", this.triggerAtTimeIndex);

            this.intervalIndex = getValidColumnIndex(path, table, "Cron", "interval");
            indicesMap.put("interval", this.intervalIndex);

            this.statusIndex = getValidColumnIndex(path, table, "Cron", "status");
            indicesMap.put("status", this.statusIndex);

            setIndicesMap(indicesMap);
        }
    }

    private final CronColumnInfo columnInfo;
    private final ProxyState proxyState;
    private static final List<String> FIELD_NAMES;
    static {
        List<String> fieldNames = new ArrayList<String>();
        fieldNames.add("id");
        fieldNames.add("type");
        fieldNames.add("triggerAtTime");
        fieldNames.add("interval");
        fieldNames.add("status");
        FIELD_NAMES = Collections.unmodifiableList(fieldNames);
    }

    CronRealmProxy(ColumnInfo columnInfo) {
        this.columnInfo = (CronColumnInfo) columnInfo;
        this.proxyState = new ProxyState(Cron.class, this);
    }

    @SuppressWarnings("cast")
    public long realmGet$id() {
        proxyState.getRealm$realm().checkIfValid();
        return (long) proxyState.getRow$realm().getLong(columnInfo.idIndex);
    }

    public void realmSet$id(long value) {
        proxyState.getRealm$realm().checkIfValid();
        proxyState.getRow$realm().setLong(columnInfo.idIndex, value);
    }

    @SuppressWarnings("cast")
    public int realmGet$type() {
        proxyState.getRealm$realm().checkIfValid();
        return (int) proxyState.getRow$realm().getLong(columnInfo.typeIndex);
    }

    public void realmSet$type(int value) {
        proxyState.getRealm$realm().checkIfValid();
        proxyState.getRow$realm().setLong(columnInfo.typeIndex, value);
    }

    @SuppressWarnings("cast")
    public long realmGet$triggerAtTime() {
        proxyState.getRealm$realm().checkIfValid();
        return (long) proxyState.getRow$realm().getLong(columnInfo.triggerAtTimeIndex);
    }

    public void realmSet$triggerAtTime(long value) {
        proxyState.getRealm$realm().checkIfValid();
        proxyState.getRow$realm().setLong(columnInfo.triggerAtTimeIndex, value);
    }

    @SuppressWarnings("cast")
    public long realmGet$interval() {
        proxyState.getRealm$realm().checkIfValid();
        return (long) proxyState.getRow$realm().getLong(columnInfo.intervalIndex);
    }

    public void realmSet$interval(long value) {
        proxyState.getRealm$realm().checkIfValid();
        proxyState.getRow$realm().setLong(columnInfo.intervalIndex, value);
    }

    @SuppressWarnings("cast")
    public boolean realmGet$status() {
        proxyState.getRealm$realm().checkIfValid();
        return (boolean) proxyState.getRow$realm().getBoolean(columnInfo.statusIndex);
    }

    public void realmSet$status(boolean value) {
        proxyState.getRealm$realm().checkIfValid();
        proxyState.getRow$realm().setBoolean(columnInfo.statusIndex, value);
    }

    public static Table initTable(ImplicitTransaction transaction) {
        if (!transaction.hasTable("class_Cron")) {
            Table table = transaction.getTable("class_Cron");
            table.addColumn(RealmFieldType.INTEGER, "id", Table.NOT_NULLABLE);
            table.addColumn(RealmFieldType.INTEGER, "type", Table.NOT_NULLABLE);
            table.addColumn(RealmFieldType.INTEGER, "triggerAtTime", Table.NOT_NULLABLE);
            table.addColumn(RealmFieldType.INTEGER, "interval", Table.NOT_NULLABLE);
            table.addColumn(RealmFieldType.BOOLEAN, "status", Table.NOT_NULLABLE);
            table.addSearchIndex(table.getColumnIndex("id"));
            table.setPrimaryKey("id");
            return table;
        }
        return transaction.getTable("class_Cron");
    }

    public static CronColumnInfo validateTable(ImplicitTransaction transaction) {
        if (transaction.hasTable("class_Cron")) {
            Table table = transaction.getTable("class_Cron");
            if (table.getColumnCount() != 5) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Field count does not match - expected 5 but was " + table.getColumnCount());
            }
            Map<String, RealmFieldType> columnTypes = new HashMap<String, RealmFieldType>();
            for (long i = 0; i < 5; i++) {
                columnTypes.put(table.getColumnName(i), table.getColumnType(i));
            }

            final CronColumnInfo columnInfo = new CronColumnInfo(transaction.getPath(), table);

            if (!columnTypes.containsKey("id")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'id' in existing Realm file. Either remove field or migrate using io.realm.internal.Table.addColumn().");
            }
            if (columnTypes.get("id") != RealmFieldType.INTEGER) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'long' for field 'id' in existing Realm file.");
            }
            if (table.isColumnNullable(columnInfo.idIndex) && table.findFirstNull(columnInfo.idIndex) != TableOrView.NO_MATCH) {
                throw new IllegalStateException("Cannot migrate an object with null value in field 'id'. Either maintain the same type for primary key field 'id', or remove the object with null value before migration.");
            }
            if (table.getPrimaryKey() != table.getColumnIndex("id")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Primary key not defined for field 'id' in existing Realm file. Add @PrimaryKey.");
            }
            if (!table.hasSearchIndex(table.getColumnIndex("id"))) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Index not defined for field 'id' in existing Realm file. Either set @Index or migrate using io.realm.internal.Table.removeSearchIndex().");
            }
            if (!columnTypes.containsKey("type")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'type' in existing Realm file. Either remove field or migrate using io.realm.internal.Table.addColumn().");
            }
            if (columnTypes.get("type") != RealmFieldType.INTEGER) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'int' for field 'type' in existing Realm file.");
            }
            if (table.isColumnNullable(columnInfo.typeIndex)) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Field 'type' does support null values in the existing Realm file. Use corresponding boxed type for field 'type' or migrate using RealmObjectSchema.setNullable().");
            }
            if (!columnTypes.containsKey("triggerAtTime")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'triggerAtTime' in existing Realm file. Either remove field or migrate using io.realm.internal.Table.addColumn().");
            }
            if (columnTypes.get("triggerAtTime") != RealmFieldType.INTEGER) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'long' for field 'triggerAtTime' in existing Realm file.");
            }
            if (table.isColumnNullable(columnInfo.triggerAtTimeIndex)) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Field 'triggerAtTime' does support null values in the existing Realm file. Use corresponding boxed type for field 'triggerAtTime' or migrate using RealmObjectSchema.setNullable().");
            }
            if (!columnTypes.containsKey("interval")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'interval' in existing Realm file. Either remove field or migrate using io.realm.internal.Table.addColumn().");
            }
            if (columnTypes.get("interval") != RealmFieldType.INTEGER) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'long' for field 'interval' in existing Realm file.");
            }
            if (table.isColumnNullable(columnInfo.intervalIndex)) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Field 'interval' does support null values in the existing Realm file. Use corresponding boxed type for field 'interval' or migrate using RealmObjectSchema.setNullable().");
            }
            if (!columnTypes.containsKey("status")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'status' in existing Realm file. Either remove field or migrate using io.realm.internal.Table.addColumn().");
            }
            if (columnTypes.get("status") != RealmFieldType.BOOLEAN) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'boolean' for field 'status' in existing Realm file.");
            }
            if (table.isColumnNullable(columnInfo.statusIndex)) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Field 'status' does support null values in the existing Realm file. Use corresponding boxed type for field 'status' or migrate using RealmObjectSchema.setNullable().");
            }
            return columnInfo;
        } else {
            throw new RealmMigrationNeededException(transaction.getPath(), "The Cron class is missing from the schema for this Realm.");
        }
    }

    public static String getTableName() {
        return "class_Cron";
    }

    public static List<String> getFieldNames() {
        return FIELD_NAMES;
    }

    @SuppressWarnings("cast")
    public static Cron createOrUpdateUsingJsonObject(Realm realm, JSONObject json, boolean update)
        throws JSONException {
        Cron obj = null;
        if (update) {
            Table table = realm.getTable(Cron.class);
            long pkColumnIndex = table.getPrimaryKey();
            long rowIndex = TableOrView.NO_MATCH;
            if (!json.isNull("id")) {
                rowIndex = table.findFirstLong(pkColumnIndex, json.getLong("id"));
            }
            if (rowIndex != TableOrView.NO_MATCH) {
                obj = new CronRealmProxy(realm.schema.getColumnInfo(Cron.class));
                ((RealmObjectProxy)obj).realmGet$proxyState().setRealm$realm(realm);
                ((RealmObjectProxy)obj).realmGet$proxyState().setRow$realm(table.getUncheckedRow(rowIndex));
            }
        }
        if (obj == null) {
            if (json.has("id")) {
                if (json.isNull("id")) {
                    obj = (CronRealmProxy) realm.createObject(Cron.class, null);
                } else {
                    obj = (CronRealmProxy) realm.createObject(Cron.class, json.getLong("id"));
                }
            } else {
                obj = (CronRealmProxy) realm.createObject(Cron.class);
            }
        }
        if (json.has("id")) {
            if (json.isNull("id")) {
                throw new IllegalArgumentException("Trying to set non-nullable field id to null.");
            } else {
                ((CronRealmProxyInterface) obj).realmSet$id((long) json.getLong("id"));
            }
        }
        if (json.has("type")) {
            if (json.isNull("type")) {
                throw new IllegalArgumentException("Trying to set non-nullable field type to null.");
            } else {
                ((CronRealmProxyInterface) obj).realmSet$type((int) json.getInt("type"));
            }
        }
        if (json.has("triggerAtTime")) {
            if (json.isNull("triggerAtTime")) {
                throw new IllegalArgumentException("Trying to set non-nullable field triggerAtTime to null.");
            } else {
                ((CronRealmProxyInterface) obj).realmSet$triggerAtTime((long) json.getLong("triggerAtTime"));
            }
        }
        if (json.has("interval")) {
            if (json.isNull("interval")) {
                throw new IllegalArgumentException("Trying to set non-nullable field interval to null.");
            } else {
                ((CronRealmProxyInterface) obj).realmSet$interval((long) json.getLong("interval"));
            }
        }
        if (json.has("status")) {
            if (json.isNull("status")) {
                throw new IllegalArgumentException("Trying to set non-nullable field status to null.");
            } else {
                ((CronRealmProxyInterface) obj).realmSet$status((boolean) json.getBoolean("status"));
            }
        }
        return obj;
    }

    @SuppressWarnings("cast")
    public static Cron createUsingJsonStream(Realm realm, JsonReader reader)
        throws IOException {
        Cron obj = realm.createObject(Cron.class);
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field id to null.");
                } else {
                    ((CronRealmProxyInterface) obj).realmSet$id((long) reader.nextLong());
                }
            } else if (name.equals("type")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field type to null.");
                } else {
                    ((CronRealmProxyInterface) obj).realmSet$type((int) reader.nextInt());
                }
            } else if (name.equals("triggerAtTime")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field triggerAtTime to null.");
                } else {
                    ((CronRealmProxyInterface) obj).realmSet$triggerAtTime((long) reader.nextLong());
                }
            } else if (name.equals("interval")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field interval to null.");
                } else {
                    ((CronRealmProxyInterface) obj).realmSet$interval((long) reader.nextLong());
                }
            } else if (name.equals("status")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field status to null.");
                } else {
                    ((CronRealmProxyInterface) obj).realmSet$status((boolean) reader.nextBoolean());
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return obj;
    }

    public static Cron copyOrUpdate(Realm realm, Cron object, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().threadId != realm.threadId) {
            throw new IllegalArgumentException("Objects which belong to Realm instances in other threads cannot be copied into this Realm instance.");
        }
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy)object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy)object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return object;
        }
        Cron realmObject = null;
        boolean canUpdate = update;
        if (canUpdate) {
            Table table = realm.getTable(Cron.class);
            long pkColumnIndex = table.getPrimaryKey();
            long rowIndex = table.findFirstLong(pkColumnIndex, ((CronRealmProxyInterface) object).realmGet$id());
            if (rowIndex != TableOrView.NO_MATCH) {
                realmObject = new CronRealmProxy(realm.schema.getColumnInfo(Cron.class));
                ((RealmObjectProxy)realmObject).realmGet$proxyState().setRealm$realm(realm);
                ((RealmObjectProxy)realmObject).realmGet$proxyState().setRow$realm(table.getUncheckedRow(rowIndex));
                cache.put(object, (RealmObjectProxy) realmObject);
            } else {
                canUpdate = false;
            }
        }

        if (canUpdate) {
            return update(realm, realmObject, object, cache);
        } else {
            return copy(realm, object, update, cache);
        }
    }

    public static Cron copy(Realm realm, Cron newObject, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
        Cron realmObject = realm.createObject(Cron.class, ((CronRealmProxyInterface) newObject).realmGet$id());
        cache.put(newObject, (RealmObjectProxy) realmObject);
        ((CronRealmProxyInterface) realmObject).realmSet$id(((CronRealmProxyInterface) newObject).realmGet$id());
        ((CronRealmProxyInterface) realmObject).realmSet$type(((CronRealmProxyInterface) newObject).realmGet$type());
        ((CronRealmProxyInterface) realmObject).realmSet$triggerAtTime(((CronRealmProxyInterface) newObject).realmGet$triggerAtTime());
        ((CronRealmProxyInterface) realmObject).realmSet$interval(((CronRealmProxyInterface) newObject).realmGet$interval());
        ((CronRealmProxyInterface) realmObject).realmSet$status(((CronRealmProxyInterface) newObject).realmGet$status());
        return realmObject;
    }

    public static Cron createDetachedCopy(Cron realmObject, int currentDepth, int maxDepth, Map<RealmModel, CacheData<RealmModel>> cache) {
        if (currentDepth > maxDepth || realmObject == null) {
            return null;
        }
        CacheData<RealmModel> cachedObject = cache.get(realmObject);
        Cron unmanagedObject;
        if (cachedObject != null) {
            // Reuse cached object or recreate it because it was encountered at a lower depth.
            if (currentDepth >= cachedObject.minDepth) {
                return (Cron)cachedObject.object;
            } else {
                unmanagedObject = (Cron)cachedObject.object;
                cachedObject.minDepth = currentDepth;
            }
        } else {
            unmanagedObject = new Cron();
            cache.put(realmObject, new RealmObjectProxy.CacheData(currentDepth, unmanagedObject));
        }
        ((CronRealmProxyInterface) unmanagedObject).realmSet$id(((CronRealmProxyInterface) realmObject).realmGet$id());
        ((CronRealmProxyInterface) unmanagedObject).realmSet$type(((CronRealmProxyInterface) realmObject).realmGet$type());
        ((CronRealmProxyInterface) unmanagedObject).realmSet$triggerAtTime(((CronRealmProxyInterface) realmObject).realmGet$triggerAtTime());
        ((CronRealmProxyInterface) unmanagedObject).realmSet$interval(((CronRealmProxyInterface) realmObject).realmGet$interval());
        ((CronRealmProxyInterface) unmanagedObject).realmSet$status(((CronRealmProxyInterface) realmObject).realmGet$status());
        return unmanagedObject;
    }

    static Cron update(Realm realm, Cron realmObject, Cron newObject, Map<RealmModel, RealmObjectProxy> cache) {
        ((CronRealmProxyInterface) realmObject).realmSet$type(((CronRealmProxyInterface) newObject).realmGet$type());
        ((CronRealmProxyInterface) realmObject).realmSet$triggerAtTime(((CronRealmProxyInterface) newObject).realmGet$triggerAtTime());
        ((CronRealmProxyInterface) realmObject).realmSet$interval(((CronRealmProxyInterface) newObject).realmGet$interval());
        ((CronRealmProxyInterface) realmObject).realmSet$status(((CronRealmProxyInterface) newObject).realmGet$status());
        return realmObject;
    }

    @Override
    public ProxyState realmGet$proxyState() {
        return proxyState;
    }

    @Override
    public int hashCode() {
        String realmName = proxyState.getRealm$realm().getPath();
        String tableName = proxyState.getRow$realm().getTable().getName();
        long rowIndex = proxyState.getRow$realm().getIndex();

        int result = 17;
        result = 31 * result + ((realmName != null) ? realmName.hashCode() : 0);
        result = 31 * result + ((tableName != null) ? tableName.hashCode() : 0);
        result = 31 * result + (int) (rowIndex ^ (rowIndex >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CronRealmProxy aCron = (CronRealmProxy)o;

        String path = proxyState.getRealm$realm().getPath();
        String otherPath = aCron.proxyState.getRealm$realm().getPath();
        if (path != null ? !path.equals(otherPath) : otherPath != null) return false;;

        String tableName = proxyState.getRow$realm().getTable().getName();
        String otherTableName = aCron.proxyState.getRow$realm().getTable().getName();
        if (tableName != null ? !tableName.equals(otherTableName) : otherTableName != null) return false;

        if (proxyState.getRow$realm().getIndex() != aCron.proxyState.getRow$realm().getIndex()) return false;

        return true;
    }

}
