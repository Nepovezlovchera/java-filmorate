    CREATE TABLE IF NOT EXISTS genre (
        genre_id BIGINT AUTO_INCREMENT PRIMARY KEY,
        genre_name VARCHAR(50) NOT NULL UNIQUE
    );

    CREATE TABLE IF NOT EXISTS mpa (
        mpa_id BIGINT AUTO_INCREMENT PRIMARY KEY,
        mpa_name VARCHAR(20) NOT NULL
    );

    CREATE TABLE IF NOT EXISTS friendship_status (
        status_id BIGINT AUTO_INCREMENT PRIMARY KEY,
        status_check VARCHAR(20) NOT NULL
    );

    CREATE TABLE IF NOT EXISTS users (
        user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_name VARCHAR(100),
        email VARCHAR(255) NOT NULL,
        login VARCHAR(100) NOT NULL,
        birthday TIMESTAMP
    );

    CREATE TABLE IF NOT EXISTS films (
        film_id BIGINT AUTO_INCREMENT PRIMARY KEY,
        film_name VARCHAR(255) NOT NULL,
        description VARCHAR(200),
        release_date TIMESTAMP,
        duration INTEGER,
        mpa_id INTEGER REFERENCES mpa(mpa_id)
    );

    CREATE TABLE IF NOT EXISTS likes (
        like_id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT REFERENCES users(user_id),
        film_id BIGINT REFERENCES films(film_id)
    );

    CREATE TABLE IF NOT EXISTS friends (
        friend_id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT REFERENCES users(user_id),
        other_user_id BIGINT REFERENCES users(user_id),
        status_id BIGINT REFERENCES friendship_status(status_id)
    );

    CREATE TABLE IF NOT EXISTS film_genres (
        film_id BIGINT REFERENCES films(film_id) ON DELETE CASCADE,
        genre_id BIGINT REFERENCES genre(genre_id) ON DELETE CASCADE,
        genre_order INTEGER DEFAULT 0,
        PRIMARY KEY (film_id, genre_id)
    );