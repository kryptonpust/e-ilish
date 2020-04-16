export let notification = function(msg) {
    var data = msg.data;
    var msg;
    if (typeof data.message === "undefined") {
        if (typeof data.text === "undefined") {
        } else {
            msg = data.text;
        }
    } else {
        msg = data.message;
    }
    if (data.error_code == "logout") {
        location.replace("/logout");
    }
    this.$notify({
        group: "notification",
        type: data.type,
        title: data.title,
        text: msg
    });
};
export let noti_error = function(err) {
    if (typeof err.response !== "undefined") {
        if (err.response.status == 401) {
            location.replace("/logout");
        }
    }
    notification.call(this, { type: "error", title: "Error", text: err });
};

export let noti_success = function(msg) {
    this.$notify({
        group: "notification",
        type: "success",
        title: "Success",
        text: msg
    });
};
export let noti_warn = function(msg) {
    this.$notify({
        group: "notification",
        type: "warn",
        title: "Warning",
        text: msg
    });
};
