package com.example.linguatrain.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.linguatrain.models.Lesson;
import com.example.linguatrain.models.Test;
import com.example.linguatrain.models.Word;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "linguatrain.db";
    private static final int DATABASE_VERSION = 3;

    // Таблицы
    private static final String TABLE_LESSONS = "lessons";
    private static final String TABLE_WORDS = "words";
    private static final String TABLE_TESTS = "tests";
    private static final String TABLE_TEST_QUESTIONS = "test_questions";
    private static final String TABLE_STUDENT_PROGRESS = "student_progress";
    private static final String TABLE_USERS = "users";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Таблица занятий
        String createLessons = "CREATE TABLE " + TABLE_LESSONS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "isUnlocked INTEGER DEFAULT 0)";
        db.execSQL(createLessons);

        // Таблица слов
        String createWords = "CREATE TABLE " + TABLE_WORDS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "en_word TEXT, " +
                "ru_translate TEXT, " +
                "lesson_id INTEGER, " +
                "FOREIGN KEY(lesson_id) REFERENCES lessons(id) ON DELETE CASCADE)";
        db.execSQL(createWords);

        // Таблица тестов (с полем isUnlocked)
        String createTests = "CREATE TABLE " + TABLE_TESTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "lesson_id INTEGER, " +
                "isUnlocked INTEGER DEFAULT 0, " +
                "FOREIGN KEY(lesson_id) REFERENCES lessons(id) ON DELETE CASCADE)";
        db.execSQL(createTests);

        // Таблица вопросов теста
        String createTestQuestions = "CREATE TABLE " + TABLE_TEST_QUESTIONS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "test_id INTEGER, " +
                "question TEXT, " +
                "correct_answer TEXT, " +
                "wrong_answer1 TEXT, " +
                "wrong_answer2 TEXT, " +
                "wrong_answer3 TEXT, " +
                "FOREIGN KEY(test_id) REFERENCES tests(id) ON DELETE CASCADE)";
        db.execSQL(createTestQuestions);

        // Таблица прогресса ученика
        String createProgress = "CREATE TABLE " + TABLE_STUDENT_PROGRESS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "lesson_id INTEGER, " +
                "test_id INTEGER, " +
                "completed INTEGER DEFAULT 0, " +
                "FOREIGN KEY(lesson_id) REFERENCES lessons(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(test_id) REFERENCES tests(id) ON DELETE CASCADE)";
        db.execSQL(createProgress);
        // Очистить старые данные прогресса
        db.delete(TABLE_STUDENT_PROGRESS, null, null);

        // Таблица пользователей
        String createUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "login TEXT UNIQUE, " +
                "password TEXT, " +
                "role TEXT)";
        db.execSQL(createUsers);

        // Добавить тестовые данные
        insertTestData(db);
    }

    private void insertTestData(SQLiteDatabase db) {
        // Пользователи
        ContentValues tutorValues = new ContentValues();
        tutorValues.put("login", "tutor");
        tutorValues.put("password", "123");
        tutorValues.put("role", "tutor");
        db.insert(TABLE_USERS, null, tutorValues);

        ContentValues studentValues = new ContentValues();
        studentValues.put("login", "student");
        studentValues.put("password", "123");
        db.insert(TABLE_USERS, null, studentValues);

        // Занятие 1
        ContentValues values = new ContentValues();
        values.put("title", "Семья и друзья");
        values.put("isUnlocked", 1);
        long lessonId = db.insert(TABLE_LESSONS, null, values);

        String[][] words = {
                {"mother", "мама"},
                {"father", "папа"},
                {"sister", "сестра"},
                {"brother", "брат"},
                {"friend", "друг"}
        };
        for (String[] w : words) {
            ContentValues wordValues = new ContentValues();
            wordValues.put("en_word", w[0]);
            wordValues.put("ru_translate", w[1]);
            wordValues.put("lesson_id", lessonId);
            db.insert(TABLE_WORDS, null, wordValues);
        }

        // Занятие 2 (заблокировано)
        ContentValues values2 = new ContentValues();
        values2.put("title", "Еда и напитки");
        values2.put("isUnlocked", 0);
        db.insert(TABLE_LESSONS, null, values2);

        // Тест 1 (разблокирован)
        ContentValues testValues = new ContentValues();
        testValues.put("title", "Тест: Семья");
        testValues.put("lesson_id", 1);
        testValues.put("isUnlocked", 1);
        long testId = db.insert(TABLE_TESTS, null, testValues);

        addTestQuestion(db, testId, "Как переводится 'mother'?", "мама", "папа", "сестра", "брат");
        addTestQuestion(db, testId, "Как переводится 'father'?", "папа", "мама", "друг", "брат");
        addTestQuestion(db, testId, "Как переводится 'sister'?", "сестра", "брат", "мама", "папа");

        // Тест 2 (заблокирован)
        ContentValues testValues2 = new ContentValues();
        testValues2.put("title", "Тест: Еда");
        testValues2.put("lesson_id", 2);
        testValues2.put("isUnlocked", 0);
        db.insert(TABLE_TESTS, null, testValues2);
    }

    private void addTestQuestion(SQLiteDatabase db, long testId, String question, String correct, String w1, String w2, String w3) {
        ContentValues values = new ContentValues();
        values.put("test_id", testId);
        values.put("question", question);
        values.put("correct_answer", correct);
        values.put("wrong_answer1", w1);
        values.put("wrong_answer2", w2);
        values.put("wrong_answer3", w3);
        db.insert(TABLE_TEST_QUESTIONS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT_PROGRESS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEST_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TESTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LESSONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // --- ЗАНЯТИЯ ---

    public List<Lesson> getAllLessons() {
        List<Lesson> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LESSONS, null);
        if (cursor.moveToFirst()) {
            do {
                Lesson lesson = new Lesson();
                lesson.setId(String.valueOf(cursor.getInt(0)));
                lesson.setTitle(cursor.getString(1));
                lesson.setUnlocked(cursor.getInt(2) == 1);
                list.add(lesson);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public long createLesson(String title) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("isUnlocked", 0);
        return db.insert(TABLE_LESSONS, null, values);
    }

    public void updateLesson(String lessonId, String title) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        db.update(TABLE_LESSONS, values, "id = ?", new String[]{lessonId});
    }

    public void deleteLesson(String lessonId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_WORDS, "lesson_id = ?", new String[]{lessonId});
        db.delete(TABLE_LESSONS, "id = ?", new String[]{lessonId});
    }

    public void clearWordsForLesson(String lessonId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_WORDS, "lesson_id = ?", new String[]{lessonId});
    }

    // --- СЛОВА ---

    public List<Word> getWordsForLesson(String lessonId) {
        List<Word> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_WORDS + " WHERE lesson_id = ?", new String[]{lessonId});
        if (cursor.moveToFirst()) {
            do {
                Word word = new Word();
                word.setId(String.valueOf(cursor.getInt(0)));
                word.setEnWord(cursor.getString(1));
                word.setRuTranslate(cursor.getString(2));
                list.add(word);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void addWordToLesson(String lessonId, String enWord, String ruTranslate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("en_word", enWord);
        values.put("ru_translate", ruTranslate);
        values.put("lesson_id", lessonId);
        db.insert(TABLE_WORDS, null, values);
    }

    // --- ТЕСТЫ ---

    public List<Test> getAllTests() {
        List<Test> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TESTS, null);
        if (cursor.moveToFirst()) {
            do {
                Test test = new Test();
                test.setId(String.valueOf(cursor.getInt(0)));
                test.setTitle(cursor.getString(1));
                test.setLessonId(cursor.getInt(2));
                test.setUnlocked(cursor.getInt(3) == 1);
                list.add(test);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public long createTest(String title, String lessonId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("lesson_id", lessonId);
        values.put("isUnlocked", 0);
        return db.insert(TABLE_TESTS, null, values);
    }

    public void updateTest(String testId, String title) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        db.update(TABLE_TESTS, values, "id = ?", new String[]{testId});
    }

    public void deleteTest(String testId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_TEST_QUESTIONS, "test_id = ?", new String[]{testId});
        db.delete(TABLE_TESTS, "id = ?", new String[]{testId});
    }

    public void clearQuestionsForTest(String testId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_TEST_QUESTIONS, "test_id = ?", new String[]{testId});
    }

    // --- ВОПРОСЫ ТЕСТА ---

    public List<Test> getQuestionsForTest(String testId) {
        List<Test> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TEST_QUESTIONS + " WHERE test_id = ?", new String[]{testId});
        if (cursor.moveToFirst()) {
            do {
                Test test = new Test();
                test.setId(String.valueOf(cursor.getInt(0)));
                test.setQuestion(cursor.getString(2));
                test.setCorrectAnswer(cursor.getString(3));
                test.setWrongAnswer1(cursor.getString(4));
                test.setWrongAnswer2(cursor.getString(5));
                test.setWrongAnswer3(cursor.getString(6));
                list.add(test);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void addQuestionToTest(String testId, String question, String correctAnswer, String w1, String w2, String w3) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("test_id", testId);
        values.put("question", question);
        values.put("correct_answer", correctAnswer);
        values.put("wrong_answer1", w1);
        values.put("wrong_answer2", w2);
        values.put("wrong_answer3", w3);
        db.insert(TABLE_TEST_QUESTIONS, null, values);
    }

    public boolean isUserExists(String login) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE login = ?", new String[]{login});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public void registerUser(String login, String password, String role) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("login", login);
        values.put("password", password);
        values.put("role", role);
        db.insert(TABLE_USERS, null, values);
    }

    // --- ПРОГРЕСС УЧЕНИКА ---

    public void markLessonCompleted(String lessonId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("lesson_id", lessonId);
        values.put("completed", 1);
        db.insert(TABLE_STUDENT_PROGRESS, null, values);
    }

    public void markTestCompleted(String testId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("test_id", testId);
        values.put("completed", 1);
        db.insert(TABLE_STUDENT_PROGRESS, null, values);
    }

    public boolean isLessonCompleted(String lessonId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_STUDENT_PROGRESS + " WHERE lesson_id = ? AND completed = 1", new String[]{lessonId});
        boolean completed = cursor.getCount() > 0;
        cursor.close();
        return completed;
    }

    public boolean isTestCompleted(String testId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_STUDENT_PROGRESS + " WHERE test_id = ? AND completed = 1", new String[]{testId});
        boolean completed = cursor.getCount() > 0;
        cursor.close();
        return completed;
    }

    public boolean isTestUnlocked(String testId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT isUnlocked FROM " + TABLE_TESTS + " WHERE id = ?", new String[]{testId});
        if (cursor.moveToFirst()) {
            boolean unlocked = cursor.getInt(0) == 1;
            cursor.close();
            return unlocked;
        }
        cursor.close();
        return false;
    }

    public void unlockNextLesson(String currentLessonId) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM " + TABLE_LESSONS + " WHERE id > ? AND isUnlocked = 0 ORDER BY id ASC LIMIT 1", new String[]{currentLessonId});
        if (cursor.moveToFirst()) {
            String nextLessonId = String.valueOf(cursor.getInt(0));
            ContentValues values = new ContentValues();
            values.put("isUnlocked", 1);
            db.update(TABLE_LESSONS, values, "id = ?", new String[]{nextLessonId});
        }
        cursor.close();
    }

    public void unlockNextTest(String currentTestId) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM " + TABLE_TESTS + " WHERE id > ? AND isUnlocked = 0 ORDER BY id ASC LIMIT 1", new String[]{currentTestId});
        if (cursor.moveToFirst()) {
            String nextTestId = String.valueOf(cursor.getInt(0));
            ContentValues values = new ContentValues();
            values.put("isUnlocked", 1);
            db.update(TABLE_TESTS, values, "id = ?", new String[]{nextTestId});
        }
        cursor.close();
    }

    public int getCompletedLessonsCount() {
        SQLiteDatabase db = getReadableDatabase();
        // Считаем только те записи, где lesson_id существует в таблице lessons
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_STUDENT_PROGRESS + " p " +
                        "JOIN " + TABLE_LESSONS + " l ON p.lesson_id = l.id " +
                        "WHERE p.lesson_id IS NOT NULL AND p.completed = 1",
                null
        );
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int getCompletedTestsCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_STUDENT_PROGRESS + " WHERE test_id IS NOT NULL AND completed = 1", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int getTotalLessonsCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_LESSONS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int getTotalTestsCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TESTS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
}