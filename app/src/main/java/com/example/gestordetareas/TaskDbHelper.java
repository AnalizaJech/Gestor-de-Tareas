package com.example.gestordetareas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDbHelper extends SQLiteOpenHelper {
    // Nombre de la base de datos
    public static final String DATABASE_NAME = "tasks.db";
    // Versión de la base de datos
    public static final int DATABASE_VERSION = 1;

    // Nombre de la tabla y columnas
    public static final String TABLE_NAME = "tasks";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TASK = "task";

    // Sentencia SQL para crear la tabla
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_TASK + " TEXT)";

    // Sentencia SQL para eliminar la tabla si existe
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    // Método para obtener todas las tareas
    public Cursor getAllTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                COLUMN_ID,
                COLUMN_TASK
        };
        return db.query(
                TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
    }

    // Método para insertar una tarea
    public long insertTask(String task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK, task);
        return db.insert(TABLE_NAME, null, values);
    }

    // Método para eliminar una tarea por su ID
    public int deleteTask(long taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(taskId) };
        return db.delete(TABLE_NAME, selection, selectionArgs);
    }

    // Método para actualizar una tarea por su ID
    public int updateTask(long taskId, String newTask) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK, newTask);
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(taskId) };
        return db.update(TABLE_NAME, values, selection, selectionArgs);
    }
}
