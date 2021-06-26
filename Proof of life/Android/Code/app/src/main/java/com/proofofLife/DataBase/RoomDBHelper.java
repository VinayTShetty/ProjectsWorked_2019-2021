package com.proofofLife.DataBase;

import android.content.Context;
import android.nfc.Tag;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import com.proofofLife.DataBaseRoomDAO.DeviceRegistation_Room;
import com.proofofLife.DataBaseRoomEntity.DeviceRegistration_DAO;

@Database(entities = {DeviceRegistation_Room.class},version =1,exportSchema = false)
public abstract  class RoomDBHelper extends RoomDatabase {
    public static final String TAG= RoomDBHelper.class.getSimpleName();
    public static final String DB_NAME="proofof_life.db";
    private static RoomDBHelper roomDBHelperINSTACE=null;

    public static synchronized RoomDBHelper getRoomDBInstance(Context context){
        if(roomDBHelperINSTACE==null){
            roomDBHelperINSTACE = buildDatabaseInstance(context);
        }
        return roomDBHelperINSTACE;
    }
    private static RoomDBHelper buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context, RoomDBHelper.class, DB_NAME)
                .build();
    }
    /**
     * interface DAO
     */
    public abstract DeviceRegistration_DAO get_deviceRegistration_dao();
}
