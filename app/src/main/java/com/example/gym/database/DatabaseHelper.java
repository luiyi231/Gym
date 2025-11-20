package com.example.gym.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.gym.models.Ejercicio;
import com.example.gym.models.Rol;
import com.example.gym.models.Rutina;
import com.example.gym.models.RutinaEjercicio;
import com.example.gym.models.Usuario;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "gym_database.db";
    private static final int DATABASE_VERSION = 1;

    // Tabla Rol
    private static final String TABLE_ROL = "rol";
    private static final String COL_ROL_ID = "id";
    private static final String COL_ROL_NOMBRE = "nombre";

    // Tabla Usuario
    private static final String TABLE_USUARIO = "usuario";
    private static final String COL_USUARIO_ID = "id";
    private static final String COL_USUARIO_NOMBRE = "nombre";
    private static final String COL_USUARIO_APELLIDO = "apellido";

    // Tabla Ejercicio
    private static final String TABLE_EJERCICIO = "ejercicio";
    private static final String COL_EJERCICIO_ID = "ejercicioId";
    private static final String COL_EJERCICIO_NOMBRE = "nombre";
    private static final String COL_EJERCICIO_REPS = "reps";
    private static final String COL_EJERCICIO_PESO = "peso";

    // Tabla Rutina
    private static final String TABLE_RUTINA = "rutina";
    private static final String COL_RUTINA_ID = "rutinaId";
    private static final String COL_RUTINA_NOMBRE = "rutinaNombre";
    private static final String COL_RUTINA_USUARIO_ID = "usuarioId";

    // Tabla RutinaEjercicio
    private static final String TABLE_RUTINA_EJERCICIO = "rutina_ejercicio";
    private static final String COL_RE_RUTINA_ID = "rutinaId";
    private static final String COL_RE_EJERCICIO_ID = "ejercicioId";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla Rol
        String createRolTable = "CREATE TABLE " + TABLE_ROL + " (" +
                COL_ROL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ROL_NOMBRE + " TEXT NOT NULL" +
                ")";
        db.execSQL(createRolTable);

        // Crear tabla Usuario (sin relación con Rol)
        String createUsuarioTable = "CREATE TABLE " + TABLE_USUARIO + " (" +
                COL_USUARIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USUARIO_NOMBRE + " TEXT NOT NULL, " +
                COL_USUARIO_APELLIDO + " TEXT NOT NULL" +
                ")";
        db.execSQL(createUsuarioTable);

        // Crear tabla Ejercicio
        String createEjercicioTable = "CREATE TABLE " + TABLE_EJERCICIO + " (" +
                COL_EJERCICIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EJERCICIO_NOMBRE + " TEXT NOT NULL, " +
                COL_EJERCICIO_REPS + " INTEGER, " +
                COL_EJERCICIO_PESO + " REAL" +
                ")";
        db.execSQL(createEjercicioTable);

        // Crear tabla Rutina
        String createRutinaTable = "CREATE TABLE " + TABLE_RUTINA + " (" +
                COL_RUTINA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RUTINA_NOMBRE + " TEXT NOT NULL, " +
                COL_RUTINA_USUARIO_ID + " INTEGER, " +
                "FOREIGN KEY(" + COL_RUTINA_USUARIO_ID + ") REFERENCES " + TABLE_USUARIO + "(" + COL_USUARIO_ID + ")" +
                ")";
        db.execSQL(createRutinaTable);

        // Crear tabla RutinaEjercicio
        String createRutinaEjercicioTable = "CREATE TABLE " + TABLE_RUTINA_EJERCICIO + " (" +
                COL_RE_RUTINA_ID + " INTEGER, " +
                COL_RE_EJERCICIO_ID + " INTEGER, " +
                "PRIMARY KEY(" + COL_RE_RUTINA_ID + ", " + COL_RE_EJERCICIO_ID + "), " +
                "FOREIGN KEY(" + COL_RE_RUTINA_ID + ") REFERENCES " + TABLE_RUTINA + "(" + COL_RUTINA_ID + "), " +
                "FOREIGN KEY(" + COL_RE_EJERCICIO_ID + ") REFERENCES " + TABLE_EJERCICIO + "(" + COL_EJERCICIO_ID + ")" +
                ")";
        db.execSQL(createRutinaEjercicioTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RUTINA_EJERCICIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RUTINA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EJERCICIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROL);
        onCreate(db);
    }

    // Métodos para Rol
    public long insertRol(Rol rol) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ROL_ID, rol.getId());
        values.put(COL_ROL_NOMBRE, rol.getNombre());
        long id = db.insert(TABLE_ROL, null, values);
        db.close();
        return id;
    }

    public List<Rol> getAllRoles() {
        List<Rol> roles = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ROL;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Rol rol = new Rol();
                rol.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ROL_ID)));
                rol.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(COL_ROL_NOMBRE)));
                roles.add(rol);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return roles;
    }

    public void deleteAllRoles() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ROL, null, null);
        db.close();
    }

    // Métodos para Usuario
    public long insertUsuario(Usuario usuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USUARIO_ID, usuario.getId());
        values.put(COL_USUARIO_NOMBRE, usuario.getNombre());
        values.put(COL_USUARIO_APELLIDO, usuario.getApellido());
        long id = db.insert(TABLE_USUARIO, null, values);
        db.close();
        return id;
    }

    public List<Usuario> getAllUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_USUARIO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Usuario usuario = new Usuario();
                usuario.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USUARIO_ID)));
                usuario.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(COL_USUARIO_NOMBRE)));
                usuario.setApellido(cursor.getString(cursor.getColumnIndexOrThrow(COL_USUARIO_APELLIDO)));
                
                // Obtener rutinas del usuario
                List<Rutina> rutinas = getRutinasByUsuarioId(usuario.getId());
                usuario.setRutinas(rutinas);
                
                usuarios.add(usuario);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return usuarios;
    }

    public void deleteAllUsuarios() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USUARIO, null, null);
        db.close();
    }

    // Métodos para Ejercicio
    public long insertEjercicio(Ejercicio ejercicio) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EJERCICIO_ID, ejercicio.getEjercicioId());
        values.put(COL_EJERCICIO_NOMBRE, ejercicio.getNombre());
        values.put(COL_EJERCICIO_REPS, ejercicio.getReps());
        values.put(COL_EJERCICIO_PESO, ejercicio.getPeso());
        long id = db.insert(TABLE_EJERCICIO, null, values);
        db.close();
        return id;
    }

    public List<Ejercicio> getAllEjercicios() {
        List<Ejercicio> ejercicios = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EJERCICIO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Ejercicio ejercicio = new Ejercicio();
                ejercicio.setEjercicioId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_EJERCICIO_ID)));
                ejercicio.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(COL_EJERCICIO_NOMBRE)));
                ejercicio.setReps(cursor.getInt(cursor.getColumnIndexOrThrow(COL_EJERCICIO_REPS)));
                ejercicio.setPeso(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_EJERCICIO_PESO)));
                ejercicios.add(ejercicio);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return ejercicios;
    }

    public void deleteAllEjercicios() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EJERCICIO, null, null);
        db.close();
    }

    // Métodos para Rutina
    public long insertRutina(Rutina rutina) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_RUTINA_ID, rutina.getRutinaId());
        values.put(COL_RUTINA_NOMBRE, rutina.getRutinaNombre());
        values.put(COL_RUTINA_USUARIO_ID, rutina.getUsuarioId());
        long id = db.insert(TABLE_RUTINA, null, values);
        db.close();
        return id;
    }

    public List<Rutina> getAllRutinas() {
        List<Rutina> rutinas = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RUTINA;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Rutina rutina = new Rutina();
                rutina.setRutinaId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_RUTINA_ID)));
                rutina.setRutinaNombre(cursor.getString(cursor.getColumnIndexOrThrow(COL_RUTINA_NOMBRE)));
                rutina.setUsuarioId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_RUTINA_USUARIO_ID)));
                
                // Obtener ejercicios de la rutina
                List<Ejercicio> ejercicios = getEjerciciosByRutinaId(rutina.getRutinaId());
                rutina.setEjercicios(ejercicios);
                
                rutinas.add(rutina);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return rutinas;
    }

    public List<Rutina> getRutinasByUsuarioId(int usuarioId) {
        List<Rutina> rutinas = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RUTINA + " WHERE " + COL_RUTINA_USUARIO_ID + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(usuarioId)});

        if (cursor.moveToFirst()) {
            do {
                Rutina rutina = new Rutina();
                rutina.setRutinaId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_RUTINA_ID)));
                rutina.setRutinaNombre(cursor.getString(cursor.getColumnIndexOrThrow(COL_RUTINA_NOMBRE)));
                rutina.setUsuarioId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_RUTINA_USUARIO_ID)));
                
                // Obtener ejercicios de la rutina
                List<Ejercicio> ejercicios = getEjerciciosByRutinaId(rutina.getRutinaId());
                rutina.setEjercicios(ejercicios);
                
                rutinas.add(rutina);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return rutinas;
    }

    public void deleteAllRutinas() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RUTINA, null, null);
        db.close();
    }

    // Métodos para RutinaEjercicio
    public long insertRutinaEjercicio(RutinaEjercicio rutinaEjercicio) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_RE_RUTINA_ID, rutinaEjercicio.getRutinaId());
        values.put(COL_RE_EJERCICIO_ID, rutinaEjercicio.getEjercicioId());
        long id = db.insert(TABLE_RUTINA_EJERCICIO, null, values);
        db.close();
        return id;
    }

    public List<Ejercicio> getEjerciciosByRutinaId(int rutinaId) {
        List<Ejercicio> ejercicios = new ArrayList<>();
        String selectQuery = "SELECT e.* FROM " + TABLE_EJERCICIO + " e " +
                "INNER JOIN " + TABLE_RUTINA_EJERCICIO + " re ON e." + COL_EJERCICIO_ID + " = re." + COL_RE_EJERCICIO_ID + " " +
                "WHERE re." + COL_RE_RUTINA_ID + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(rutinaId)});

        if (cursor.moveToFirst()) {
            do {
                Ejercicio ejercicio = new Ejercicio();
                ejercicio.setEjercicioId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_EJERCICIO_ID)));
                ejercicio.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(COL_EJERCICIO_NOMBRE)));
                ejercicio.setReps(cursor.getInt(cursor.getColumnIndexOrThrow(COL_EJERCICIO_REPS)));
                ejercicio.setPeso(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_EJERCICIO_PESO)));
                ejercicios.add(ejercicio);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return ejercicios;
    }

    public void deleteAllRutinaEjercicios() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RUTINA_EJERCICIO, null, null);
        db.close();
    }

    // Método para limpiar todas las tablas
    public void clearAllTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RUTINA_EJERCICIO, null, null);
        db.delete(TABLE_RUTINA, null, null);
        db.delete(TABLE_EJERCICIO, null, null);
        db.delete(TABLE_USUARIO, null, null);
        db.delete(TABLE_ROL, null, null);
        db.close();
    }
}
