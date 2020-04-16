var mysql = require("mysql");

var hat = require("hat").rack();

var pool = mysql.createPool({
  connectionLimit: 10,
  //host: "172.17.0.2",
  host: "103.48.119.116",
  user: "thebrosl_chat",
  password: "ice@pust",
  database: "thebrosl_chat",
  charset : 'utf8mb4',
});

//INSERT INTO Users (ID, token, created_at) VALUES (NULL, '54545454544', current_timestamp());
addChat = function(callback, values) {
  pool.getConnection(function(err, connection) {
    if (err) throw(err);
    var token = hat();
    connection.query(
      "INSERT INTO `chats` (`ID`, `user_from`, `user_to`, `message`, `msg_type`,`origin`, `time`) VALUES (NULL, ?, ?, ?, ?, ?,?);",
      values,
      function(error, results, fields) {
        connection.release();
        if (error) throw(error);
        callback(results.insertId);
      }
    );
  });
};

getChatwithTime =function(id,time,callback) {
  pool.getConnection(function(err, connection) {
    if (err) throw(err);
    var token = hat();
    
    connection.query(
      "SELECT `message`,`msg_type`,`origin`,`time` from chats where (user_from = ? OR user_to =?) AND time > ?;",[id,id,time.toString()],
      function(error, results, fields) {
        connection.release();
        if (error) throw(error);
        callback(results);
      }
    );
  });
};

function randomEl(list) {
  var i = Math.floor(Math.random() * list.length);
  return list[i];
}

pending_increase = function(id,callback) {
  pool.getConnection(function(err, connection) {
    if (err) throw err;
    var token = hat();
    connection.query(
      "UPDATE `chat_users` SET `pending` =`pending`+1 WHERE ID=?;",
      [id],
      function(error, results, fields) {
        connection.release();
        if (error) throw error;
        callback(results,fields);
      }
    );
  });
};

get_pending = function(id,callback) {
  pool.getConnection(function(err, connection) {
    if (err) throw err;
    var token = hat();
    connection.query(
      "SELECT `pending` FROM chat_users WHERE ID = ?",
      [id],
      function(error, results, fields) {
        connection.release();
        if (error) throw error;
        callback(results,fields);
      }
    );
  });
};

reset_pending = function(id,callback) {
  pool.getConnection(function(err, connection) {
    if (err) throw err;
    var token = hat();
    connection.query(
      "UPDATE `chat_users` SET `pending` = 0 WHERE ID=?;",
      [id],
      function(error, results, fields) {
        connection.release();
        if (error) throw error;
        callback(results,fields);
      }
    );
  });
};
deleteuser = function(id,callback) {
  pool.getConnection(function(err, connection) {
    if (err) throw err;
    connection.query(
      "DELETE FROM chat_users WHERE ID=?;",
      [id],
      function(error, results, fields) {
        connection.release();
        if (error) throw error;
        callback(results);
      }
    );
  });
};
register = function(callback) {
  var name=randomEl(adjectives)+' '+randomEl(nouns);
  pool.getConnection(function(err, connection) {
    if (err) throw err;
    var token = hat();
    connection.query(
      "INSERT INTO chat_users (ID,name,token) VALUES (NULL,?,?);",
      [name,token],
      function(error, results, fields) {
        connection.release();
        if (error) throw error;
        callback(results.insertId, token);
      }
    );
  });
};
isRegistered = function(token, callback) {
  pool.getConnection(function(err, connection) {
    if (err) throw err;
    connection.query(
      "SELECT ID FROM chat_users WHERE token = ?",
      token,
      function(error, results, fields) {
        connection.release();
        if (error) throw error;
        if (results === undefined || results.length == 0) {
          callback(-1, false);
        } else {
          callback(results[0].ID, true);
        }
      }
    );
  });
};
module.exports = {
  pool: pool,
  isValid: isRegistered,
  register: register,
  addChat: addChat,
  getTimeChat: getChatwithTime,
  pending_increase:pending_increase,
  get_pending:get_pending,
  reset_pending:reset_pending,
  deleteuser:deleteuser,
};


var adjectives = ["adamant", "adroit", "amatory", "animistic", "antic", "arcadian", "baleful", "bellicose", "bilious", "boorish", "calamitous", "caustic", "cerulean", "comely", "concomitant", "contumacious", "corpulent", "crapulous", "defamatory", "didactic", "dilatory", "dowdy", "efficacious", "effulgent", "egregious", "endemic", "equanimous", "execrable", "fastidious", "feckless", "fecund", "friable", "fulsome", "garrulous", "guileless", "gustatory", "heuristic", "histrionic", "hubristic", "incendiary", "insidious", "insolent", "intransigent", "inveterate", "invidious", "irksome", "jejune", "jocular", "judicious", "lachrymose", "limpid", "loquacious", "luminous", "mannered", "mendacious", "meretricious", "minatory", "mordant", "munificent", "nefarious", "noxious", "obtuse", "parsimonious", "pendulous", "pernicious", "pervasive", "petulant", "platitudinous", "precipitate", "propitious", "puckish", "querulous", "quiescent", "rebarbative", "recalcitant", "redolent", "rhadamanthine", "risible", "ruminative", "sagacious", "salubrious", "sartorial", "sclerotic", "serpentine", "spasmodic", "strident", "taciturn", "tenacious", "tremulous", "trenchant", "turbulent", "turgid", "ubiquitous", "uxorious", "verdant", "voluble", "voracious", "wheedling", "withering", "zealous"];
var nouns = ["ninja", "chair", "pancake", "statue", "unicorn", "rainbows", "laser", "senor", "bunny", "captain", "nibblets", "cupcake", "carrot", "gnomes", "glitter", "potato", "salad", "toejam", "curtains", "beets", "toilet", "exorcism", "stick figures", "mermaid eggs", "sea barnacles", "dragons", "jellybeans", "snakes", "dolls", "bushes", "cookies", "apples", "ice cream", "ukulele", "kazoo", "banjo", "opera singer", "circus", "trampoline", "carousel", "carnival", "locomotive", "hot air balloon", "praying mantis", "animator", "artisan", "artist", "colorist", "inker", "coppersmith", "director", "designer", "flatter", "stylist", "leadman", "limner", "make-up artist", "model", "musician", "penciller", "producer", "scenographer", "set decorator", "silversmith", "teacher", "auto mechanic", "beader", "bobbin boy", "clerk of the chapel", "filling station attendant", "foreman", "maintenance engineering", "mechanic", "miller", "moldmaker", "panel beater", "patternmaker", "plant operator", "plumber", "sawfiler", "shop foreman", "soaper", "stationary engineer", "wheelwright", "woodworkers"];
