
package com.raxdenstudios.cron.model;

import android.os.Parcelable;
import android.os.Parcelable.Creator;
import org.parceler.Generated;
import org.parceler.IdentityCollection;
import org.parceler.ParcelWrapper;
import org.parceler.ParcelerRuntimeException;

@Generated(value = "org.parceler.ParcelAnnotationProcessor", date = "2016-06-24T22:12+0200")
@SuppressWarnings({
    "unchecked",
    "deprecation"
})
public class Cron$$Parcelable
    implements Parcelable, ParcelWrapper<com.raxdenstudios.cron.model.Cron>
{

    private com.raxdenstudios.cron.model.Cron cron$$1;
    @SuppressWarnings("UnusedDeclaration")
    public final static Cron$$Parcelable.Creator$$0 CREATOR = new Cron$$Parcelable.Creator$$0();

    public Cron$$Parcelable(com.raxdenstudios.cron.model.Cron cron$$3) {
        cron$$1 = cron$$3;
    }

    @Override
    public void writeToParcel(android.os.Parcel parcel$$0, int flags) {
        write(cron$$1, parcel$$0, flags, new IdentityCollection());
    }

    public static void write(com.raxdenstudios.cron.model.Cron cron$$2, android.os.Parcel parcel$$1, int flags$$0, IdentityCollection identityMap$$0) {
        int identity$$0 = identityMap$$0 .getKey(cron$$2);
        if (identity$$0 != -1) {
            parcel$$1 .writeInt(identity$$0);
        } else {
            parcel$$1 .writeInt(identityMap$$0 .put(cron$$2));
            parcel$$1 .writeLong(cron$$2 .getTriggerAtTime());
            parcel$$1 .writeLong(cron$$2 .getInterval());
            parcel$$1 .writeLong(cron$$2 .getId());
            parcel$$1 .writeInt(cron$$2 .getType());
            parcel$$1 .writeInt((cron$$2 .isStatus()? 1 : 0));
        }
    }

    @Override
    public int describeContents() {
        return  0;
    }

    @Override
    public com.raxdenstudios.cron.model.Cron getParcel() {
        return cron$$1;
    }

    public static com.raxdenstudios.cron.model.Cron read(android.os.Parcel parcel$$3, IdentityCollection identityMap$$1) {
        int identity$$1 = parcel$$3 .readInt();
        if (identityMap$$1 .containsKey(identity$$1)) {
            if (identityMap$$1 .isReserved(identity$$1)) {
                throw new ParcelerRuntimeException("An instance loop was detected whild building Parcelable and deseralization cannot continue.  This error is most likely due to using @ParcelConstructor or @ParcelFactory.");
            }
            return identityMap$$1 .get(identity$$1);
        } else {
            com.raxdenstudios.cron.model.Cron cron$$4;
            int reservation$$0 = identityMap$$1 .reserve();
            cron$$4 = new com.raxdenstudios.cron.model.Cron();
            identityMap$$1 .put(reservation$$0, cron$$4);
            cron$$4 .setTriggerAtTime(parcel$$3 .readLong());
            cron$$4 .setInterval(parcel$$3 .readLong());
            cron$$4 .setId(parcel$$3 .readLong());
            cron$$4 .setType(parcel$$3 .readInt());
            cron$$4 .setStatus((parcel$$3 .readInt() == 1));
            return cron$$4;
        }
    }

    public final static class Creator$$0
        implements Creator<Cron$$Parcelable>
    {


        @Override
        public Cron$$Parcelable createFromParcel(android.os.Parcel parcel$$2) {
            return new Cron$$Parcelable(read(parcel$$2, new IdentityCollection()));
        }

        @Override
        public Cron$$Parcelable[] newArray(int size) {
            return new Cron$$Parcelable[size] ;
        }

    }

}
