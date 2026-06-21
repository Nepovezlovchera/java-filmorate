-- ========== СОЗДАНИЕ ТАБЛИЦ ==========

CREATE TABLE IF NOT EXISTS genre (
    genre_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    genre_name VARCHAR(50) NOT NULL
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
    birthday DATE
);

CREATE TABLE IF NOT EXISTS films (
    film_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    film_name VARCHAR(255) NOT NULL,
    description VARCHAR(200),
    release_date DATE,
    duration INTEGER,
    mpa_id BIGINT,
    FOREIGN KEY (mpa_id) REFERENCES mpa(mpa_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS likes (
    like_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    film_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS friends (
    friend_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    other_user_id BIGINT,
    status_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (other_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (status_id) REFERENCES friendship_status(status_id)
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_id BIGINT,
    genre_id BIGINT,
    genre_order INTEGER DEFAULT 0,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genre(genre_id) ON DELETE CASCADE
);