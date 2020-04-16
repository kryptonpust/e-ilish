var db = require("./database");

var uptables = [
  `CREATE TABLE chat_users (
      ID INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    token varchar(255) NOT NULL UNIQUE,
    created_at TIMESTAMP) ENGINE = InnoDB;`,
  `CREATE TABLE chats(
      ID INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
      user_from INT UNSIGNED,
      user_to INT UNSIGNED,
      message TEXT,
      msg_type varchar(20),
      origin SMALLINT,
      time TIMESTAMP,
      CONSTRAINT fk_user_from
		    FOREIGN KEY (user_from) REFERENCES chat_users (ID)
		    ON DELETE CASCADE
        ON UPDATE RESTRICT,
      CONSTRAINT fk_user_to
		    FOREIGN KEY (user_to) REFERENCES chat_users (ID)
		    ON DELETE CASCADE
		    ON UPDATE RESTRICT) ENGINE = InnoDB;`
];
var droptables = [`DROP TABLE chat_users`, `DROP TABLE chats`];
var seedtables = [
  `INSERT INTO chat_users (ID, token) VALUES (1,?);`
];
switch (process.argv[2]) {
  case "up":
    uptables.forEach(function(val, index) {
      db.pool.getConnection(function(err, connection) {
        if (err) throw err;
        connection.query(val, function(error, results, fields) {
          connection.release();
          if (error) throw error;
          if (index == uptables.length - 1) {
            process.exit(0);
          }
        });
      });
    });
    break;
  case "drop":
    droptables.forEach(function(val, index) {
      db.pool.getConnection(function(err, connection) {
        if (err) throw err;
        connection.query(val, function(error, results, fields) {
          connection.release();
          if (error) throw error;

          if (index == droptables.length - 1) {
            process.exit(0);
          }
        });
      });
    });
    break;
  case "seed":
    var hat = require("hat").rack();
    seedtables.forEach(function(val, index) {
      db.pool.getConnection(function(err, connection) {
        if (err) throw err;
        connection.query(val, hat(), function(error, results, fields) {
          connection.release();
          if (error) throw error;

          if (index == seedtables.length - 1) {
            process.exit(0);
          }
        });
      });
    });
}
