package com.cyandev.androidproficiencyexercise.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.cyandev.androidproficiencyexercise.model.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyandev on 2016/12/9.
 */
public class EntityDAO implements BaseDAO<Entity, String> {

    public static final class Columns implements BaseColumns {
        public static final String TABLE_NAME = "entity";
        public static final String COLUMN_NAME_PUBLISH_AT = "publish_at";
        public static final String COLUMN_NAME_SOURCE = "source";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_AUTHOR = "author";
    }

    // Dependency table declarations
    private static final class ImageTableColumns implements BaseColumns {
        public static final String TABLE_NAME = "entity_image";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_ENTITY_ID = "entity_id";
    }

    private SQLiteDatabase db;

    public EntityDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public static void create(SQLiteDatabase db) {
        // Create 'entity' table.
        db.execSQL(
                "CREATE TABLE " + EntityDAO.Columns.TABLE_NAME + " (" +
                Columns._ID + " INTEGER PRIMARY KEY," +
                Columns.COLUMN_NAME_PUBLISH_AT + " TEXT," +
                Columns.COLUMN_NAME_SOURCE + " TEXT," +
                Columns.COLUMN_NAME_TYPE + " TEXT," +
                Columns.COLUMN_NAME_URL + " TEXT," +
                Columns.COLUMN_NAME_TITLE + " TEXT," +
                Columns.COLUMN_NAME_AUTHOR + " TEXT);"
        );

        // Create 'image' table.
        db.execSQL(
                "CREATE TABLE " + ImageTableColumns.TABLE_NAME + " (" +
                ImageTableColumns._ID + " INTEGER PRIMARY KEY," +
                ImageTableColumns.COLUMN_NAME_URL + " TEXT," +
                ImageTableColumns.COLUMN_NAME_ENTITY_ID + " TEXT);"
        );
    }

    public static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + Columns.TABLE_NAME + ";");
            db.execSQL("DROP TABLE IF EXISTS " + ImageTableColumns.TABLE_NAME + ";");
            create(db);
        }
    }

    @Override
    public long insert(Entity entity) {
        ContentValues values = new ContentValues();
        values.put(Columns.COLUMN_NAME_PUBLISH_AT, entity.publishedAt);
        values.put(Columns.COLUMN_NAME_SOURCE, entity.source);
        values.put(Columns.COLUMN_NAME_TYPE, entity.type);
        values.put(Columns.COLUMN_NAME_URL, entity.url);
        values.put(Columns.COLUMN_NAME_TITLE, entity.title);
        values.put(Columns.COLUMN_NAME_AUTHOR, entity.author);

        long id = db.insert(Columns.TABLE_NAME, null, values);

        if (entity.images != null && entity.images.size() > 0) {
            for (String url : entity.images) {
                ContentValues values2 = new ContentValues();
                values2.put(ImageTableColumns.COLUMN_NAME_ENTITY_ID, id);
                values2.put(ImageTableColumns.COLUMN_NAME_URL, url);

                db.insert(ImageTableColumns.TABLE_NAME, null, values2);
            }
        }

        return id;
    }

    @Override
    public void delete(String category) {
        db.execSQL(
                "DELETE FROM " + ImageTableColumns.TABLE_NAME +
                " WHERE " + ImageTableColumns.COLUMN_NAME_ENTITY_ID +
                " IN (SELECT " + Columns._ID +
                " FROM " + Columns.TABLE_NAME +
                " WHERE " + Columns.COLUMN_NAME_TYPE +
                " = '" + category + "');"
        );
        db.delete(Columns.TABLE_NAME,
                Columns.COLUMN_NAME_TYPE + " = ?",
                new String[] { category });
    }

    @Override
    public void update(String category, Entity entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Entity> query(String category) {
        String[] projection = {
                Columns._ID,
                Columns.COLUMN_NAME_PUBLISH_AT,
                Columns.COLUMN_NAME_SOURCE,
                Columns.COLUMN_NAME_TYPE,
                Columns.COLUMN_NAME_URL,
                Columns.COLUMN_NAME_TITLE,
                Columns.COLUMN_NAME_AUTHOR
        };

        String[] imageProjection = {
                ImageTableColumns.COLUMN_NAME_URL
        };

        Cursor c = db.query(Columns.TABLE_NAME, projection,
                Columns.COLUMN_NAME_TYPE + " = ?",
                new String[] { category },
                null, null, Columns._ID + " ASC");

        if (c.getCount() < 1 || !c.moveToFirst()) {
            c.close();
            return null;
        }

        List<Entity> entities = new ArrayList<>();

        do {
            Entity entity = new Entity();
            entity.publishedAt = c.getString(c.getColumnIndex(Columns.COLUMN_NAME_PUBLISH_AT));
            entity.source = c.getString(c.getColumnIndex(Columns.COLUMN_NAME_SOURCE));
            entity.type = c.getString(c.getColumnIndex(Columns.COLUMN_NAME_TYPE));
            entity.url = c.getString(c.getColumnIndex(Columns.COLUMN_NAME_URL));
            entity.title = c.getString(c.getColumnIndex(Columns.COLUMN_NAME_TITLE));
            entity.author = c.getString(c.getColumnIndex(Columns.COLUMN_NAME_AUTHOR));

            // Join query image table.
            Cursor c2 = db.query(ImageTableColumns.TABLE_NAME, imageProjection,
                    ImageTableColumns.COLUMN_NAME_ENTITY_ID + " = ?",
                    new String[] { "" + c.getLong(c.getColumnIndex(Columns._ID)) },
                    null, null, Columns._ID + " ASC");

            if (c2.getCount() > 0 && c2.moveToFirst()) {
                entity.images = new ArrayList<>();
                do {
                    String url = c2.getString(c2.getColumnIndex(ImageTableColumns.COLUMN_NAME_URL));
                    entity.images.add(url);
                } while (c2.moveToNext());
            }

            c2.close();

            entities.add(entity);
        } while (c.moveToNext());

        c.close();

        return entities;
    }

}
