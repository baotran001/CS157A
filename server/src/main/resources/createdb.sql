USE quizmedb;

CREATE TABLE Users(
    uid VARCHAR(15) PRIMARY KEY, 
    password VARCHAR(30) NOT NULL
);

CREATE TABLE Folder(
    fid VARCHAR(15) PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    author VARCHAR(15) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE UsersCreatesFolder(
    uid VARCHAR(15) NOT NULL,
    fid VARCHAR(15) NOT NULL,
    PRIMARY KEY (uid, fid),
    FOREIGN KEY (uid) REFERENCES Users(uid) ON DELETE CASCADE,
    FOREIGN KEY (fid) REFERENCES Folder(fid) ON DELETE CASCADE
);

CREATE TABLE Sets(
    sid VARCHAR(15) PRIMARY KEY,
    name VARCHAR(70) NOT NULL,
    author VARCHAR(15) NOT NULL,
    date DATE NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE UserCreatesSets(
    uid VARCHAR(15) NOT NULL,
    sid VARCHAR(15) NOT NULL,
    PRIMARY KEY (uid, sid),
    FOREIGN KEY (uid) REFERENCES Users(uid) ON DELETE CASCADE,
    FOREIGN KEY (sid) REFERENCES Sets(sid) ON DELETE CASCADE
);

CREATE TABLE FolderHasSets(
    fid VARCHAR(15) NOT NULL,
    sid VARCHAR(15) NOT NULL,
    PRIMARY KEY (fid, sid),
    FOREIGN KEY (fid) REFERENCES Folder(fid) ON DELETE CASCADE,
    FOREIGN KEY (sid) REFERENCES Sets(sid) ON DELETE CASCADE
);

CREATE TABLE Tag(
    tid VARCHAR(15) PRIMARY KEY,
    tag_name VARCHAR(50)
);

CREATE TABLE SetHasTag(
    sid VARCHAR(15) NOT NULL,
    tid VARCHAR(15) NOT NULL,
    PRIMARY KEY (sid, tid),
    FOREIGN KEY (sid) REFERENCES Sets(sid) ON DELETE CASCADE,
    FOREIGN KEY (tid) REFERENCES Tag(tid) ON DELETE CASCADE
);

CREATE TABLE Flashcards(
    flashid VARCHAR(15) PRIMARY KEY,
    favorite CHAR(1) NOT NULL DEFAULT 'N',
    front VARCHAR(255) NOT NULL
);

CREATE TABLE FrontHasBack(
    front VARCHAR(255) PRIMARY KEY,
    back VARCHAR(255) NOT NULL
);

CREATE TABLE SetHasFlashcards(
    sid VARCHAR(15) NOT NULL, 
    flashid VARCHAR(15) NOT NULL, 
    PRIMARY KEY (sid, flashid),
    FOREIGN KEY (sid) REFERENCES Sets(sid) ON DELETE CASCADE,
    FOREIGN KEY (flashid) REFERENCES Flashcards(flashid) ON DELETE CASCADE
);

CREATE TABLE Reviews(
    rid VARCHAR(15) PRIMARY KEY,
    star INTEGER NOT NULL,
    author VARCHAR(15) NOT NULL,
    date DATE NOT NULL,
    text VARCHAR(255) NOT NULL
);

CREATE TABLE SetsHasReviews(
    sid VARCHAR(15) NOT NULL, 
    rid VARCHAR(15) NOT NULL, 
    PRIMARY KEY (sid, rid),
    FOREIGN KEY (sid) REFERENCES Sets(sid) ON DELETE CASCADE,
    FOREIGN KEY (rid) REFERENCES Reviews(rid) ON DELETE CASCADE
);

CREATE TABLE UserWritesReviews(
    uid VARCHAR(15) NOT NULL, 
    rid VARCHAR(15) NOT NULL, 
    PRIMARY KEY (uid, rid),
    FOREIGN KEY (uid) REFERENCES Users(uid) ON DELETE CASCADE,
    FOREIGN KEY (rid) REFERENCES Reviews(rid) ON DELETE CASCADE
);

CREATE TABLE ReviewsHasLikesList(
    rid VARCHAR(15) NOT NULL, 
    uid VARCHAR(15) NOT NULL, 
    PRIMARY KEY (rid, uid),
    FOREIGN KEY (rid) REFERENCES Reviews(rid) ON DELETE CASCADE,
    FOREIGN KEY (uid) REFERENCES Users(uid) ON DELETE CASCADE
);

CREATE TABLE ReviewsHasComments(
    rid VARCHAR(15) NOT NULL,
    cid VARCHAR(15) NOT NULL,
    date DATE NOT NULL,
    author VARCHAR(15) NOT NULL,
    text VARCHAR(255) NOT NULL,
    PRIMARY KEY (rid, cid),
    INDEX idx_cid (cid),
    FOREIGN KEY (rid) REFERENCES Reviews(rid) ON DELETE CASCADE
);

CREATE TABLE UserHasFollowingList(
    uid VARCHAR(15) NOT NULL, 
    fid VARCHAR(15) NOT NULL, 
    PRIMARY KEY (uid, fid),
    FOREIGN KEY (uid) REFERENCES Users(uid) ON DELETE CASCADE,
    FOREIGN KEY (fid) REFERENCES Users(uid) ON DELETE CASCADE
);

INSERT INTO Tag (tid, tag_name) VALUES ('1', 'English');
INSERT INTO Tag (tid, tag_name) VALUES ('2', 'Math');
INSERT INTO Tag (tid, tag_name) VALUES ('3', 'Science');
INSERT INTO Tag (tid, tag_name) VALUES ('4', 'History');