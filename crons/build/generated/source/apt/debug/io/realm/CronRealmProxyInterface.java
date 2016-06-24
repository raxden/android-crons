package io.realm;


public interface CronRealmProxyInterface {
    public long realmGet$id();
    public void realmSet$id(long value);
    public int realmGet$type();
    public void realmSet$type(int value);
    public long realmGet$triggerAtTime();
    public void realmSet$triggerAtTime(long value);
    public long realmGet$interval();
    public void realmSet$interval(long value);
    public boolean realmGet$status();
    public void realmSet$status(boolean value);
}
