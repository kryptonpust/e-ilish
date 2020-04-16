<template>
  <vue-scheduler
    :event-dialog-config="dialogConfig"
    @event-created="eventCreated"
    @event-deleted="eventDeleted"
    @event-updated="eventUpdated"
    @event-clicked="eventClicked"
    :available-views="['month']"
    :events="events"
    eventDisplay="header"
  />
</template>
<script>
import * as notihelper from "../NotifiactionHandler.js";

export default {
  data() {
    return {
      events: [],
      dialogConfig: {
        fields: [
          {
            name: "header",
            label: "Notification Title",
            required: true
          },
          {
            name: "body",
            type: "textarea",
            label: "Notification Message",
            required: true
          }
        ]
      }
    };
  },
  mounted() {
    this.fetchdata();
  },
  methods: {
    fetchdata() {
      axios
        .get("/api/getnotifications")
        .then(data => {
          this.events = data.data.data;
        })
        .catch(err => {
          notihelper.noti_error.call(this, err);
        });
    },
    eventCreated(event) {
      axios
        .post("/api/addnotification", JSON.stringify(event), {
          headers: { "content-type": "application/json" }
        })
        .then(data => {
          this.$root.$emit("update");
          notihelper.notification.call(this, data);
          this.fetchdata();
        })
        .catch(error => {
          notihelper.noti_error.call(this, error);
        });
      this.dialogConfig.fields[0].value = null;
      this.dialogConfig.fields[1].value = null;
    },
    eventDeleted(event, id) {
      axios
        .delete("/api/deletenotification?id=" + id, {
          headers: { "content-type": "application/json" }
        })
        .then(data => {
          this.$root.$emit("update");
          notihelper.notification.call(this, data);
          for (var i = 0; i < this.events.length; i++) {
            if (this.events[i].id === id) {
              this.events.splice(i, 1);
              break;
            }
          }
        })
        .catch(error => {
          notihelper.noti_error.call(this, error);
        });

      this.dialogConfig.fields[0].value = null;
      this.dialogConfig.fields[1].value = null;
    },
    eventClicked(event, key) {
      // console.log("Event clicked : " + key);
      // console.log(event);
    },
    eventUpdated(event, id) {
      var temp = event;
      temp.id = id;
      axios
        .post("/api/editnotification", JSON.stringify(temp), {
          headers: { "content-type": "application/json" }
        })
        .then(data => {
          this.$root.$emit("update");
          notihelper.notification.call(this, data);
          for (var i = 0; i < this.events.length; i++) {
            //console.log(`${this.events[i].id}===${id}`);
            if (this.events[i].id === id) {
              for (var k in event) {
                if (k !== "endTime") this.events[i][k] = event[k];
              }
              break;
            }
          }
        })
        .catch(error => {
          console.log(error);
          notihelper.noti_error.call(this, error);
        });

      this.dialogConfig.fields[0].value = null;
      this.dialogConfig.fields[1].value = null;
    }
  }
};
</script>
