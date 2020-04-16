<template>
  <div class="chat bg-white">
    <div class="users">
      <div class="spinner" v-if="loading"></div>
      <div v-else class="user-list">
        <!-- <div class="title">Messenger</div>
        <div class="hr-line"></div>-->
        <ul>
          <li
            v-for="(user) in sortedData"
            v-bind:key="user.ID"
            :class="{'selected': selected == user.ID}"
            @click="onSelected(user.ID)"
          >
            <div class="user">
              <img src="/svg/boy.svg">
              <div class="info">
                <span class="name">{{user.name}}</span>
                <span>ID: {{user.ID}}</span>
              </div>
              <div class="actions">
                <popover :name="`pop`+user.ID">
                  <label class="label label-flex">
                    Edit Name
                    <a class="button is-danger" @click="del(user.ID)">Delete</a>
                  </label>
                  <input class="input" type="text" v-model="editname">
                  <a class="button is-success is-fullwidth" @click="savename(user.ID)">Save</a>
                </popover>
                <i
                  class="fas fa-user-edit fa-2x"
                  v-popover.right="{ name: `pop`+user.ID }"
                  @click="pop(user.name)"
                ></i>

                <div class="tags has-addons" v-if="user.pending">
                  <span class="tag is-success">New</span>
                  <span class="tag is-dark">{{user.pending}}</span>
                </div>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </div>
    <div class="messages">
      <message-component v-bind:load="selected"></message-component>
      <type-component v-if="selected" v-bind:type="selected"></type-component>
    </div>
  </div>
</template>

<script>
import TypeComponent from "./TypeComponent.vue";
import MessageComponent from "./MessageComponent.vue";
import * as notihelper from "../NotifiactionHandler.js";

export default {
  props: ["message"],
  components: { MessageComponent, TypeComponent },
  data() {
    return {
      data: null,
      loading: true,
      selected: null,
      editname: null
    };
  },
  computed: {
    sortedData: function() {
      return this.data.slice().sort((a, b) => {
        // console.log(new Date(a.updated_at) > new Date(b.updated_at));
        return new Date(a.updated_at) < new Date(b.updated_at);
      });
    }
  },
  mounted() {
    this.getuserlist();
    this.$root.$on(
      "reset_pending",
      function(id) {
        if (this.data) {
          this.data.forEach(element => {
            if (element.ID == id) {
              element.pending = 0;
            }
          });
        }
      }.bind(this)
    );
    this.$root.$on(
      "update_pending",
      function(id) {
        if (this.data) {
          this.data.forEach(element => {
            if (element.ID == id) {
              element.pending++;
            }
          });
        }
      }.bind(this)
    );
    this.$root.$on(
      "nmsg",
      function(msg, id) {
        if (this.data) {
          this.data.forEach(element => {
            if (element.ID == id) {
              element.updated_at = msg.time;
            }
          });
        }
      }.bind(this)
    );
    this.$root.$on(
      "refresh",
      function() {
        this.getuserlist();
      }.bind(this)
    );
  },
  methods: {
    onSelected(id) {
      this.selected = id;
      this.$root.$emit("selected", this.selected);
    },
    pop(name) {
      this.editname = name;
    },
    savename(id) {
      axios
        .post(
          "/api/editname",
          JSON.stringify({ id: id, name: this.editname }),
          {
            headers: { "content-type": "application/json" }
          }
        )
        .then(data => {
          notihelper.notification.call(this, data);

          this.data.forEach(element => {
            if (element.ID == id) {
              element.name = this.editname;
            }
          });
        })
        .catch(error => {
          notihelper.noti_error.call(this, error);
        });
    },
    del(id) {
      this.selected = null;
      this.$root.$emit("delete", id);
    },
    getuserlist() {
      axios
        .get("/api/chatusers")
        .then(data => {
          this.data = data.data;
          for (var i of this.data) {
            if (i.pending != 0) {
              var audio = new Audio("/sound.mp3");
              audio.play();
              this.$notify({
                group: "notification",
                type: "warn",
                title: "Unread message",
                text: "You have some unread message!!"
              });
              break;
            }
          }
          this.loading = false;
        })
        .catch(err => {
          notihelper.noti_error.call(this, err);
        });
    }
  }
};
</script>


<style lang="scss" scoped>
.chat {
  display: flex;
  width: 100%;
  height: 94%;
}
.hr-line {
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  z-index: 201;
}
.users {
  flex: 2;
  border-right: 1px solid rgba(0, 0, 0, 0.1);
  z-index: 20;
  display: flex;
  align-items: center;
  justify-content: center;
  .user {
    padding: 5px;
    display: flex;
    align-items: center;
    flex: 3;
    img {
      height: 5em;
      width: 5em;
    }
    .info {
      flex-grow: 1;
      display: flex;
      flex-direction: column;
      justify-content: flex-start;
      .name {
        color: rgba(0, 0, 0, 1);
        font-size: 15px;
        font-weight: 400;
        line-height: 1.4;
      }
    }
    .fa-user-edit {
      align-self: center;
      padding: 5px;
    }
    .fa-user-edit:hover {
      color: red;
      z-index: 1000;
    }
    .input {
      margin-bottom: 5px;
    }
    .actions {
      display: flex;
      flex-direction: column;
      justify-content: center;
      .label-flex {
        display: flex;
        justify-content: space-between;
        align-items: baseline;
      }
    }
  }
  .selected {
    background-color: rgba(0, 0, 0, 0.05);
  }
  li:hover {
    background-color: rgba(0, 0, 0, 0.2);
  }
  .user-list {
    align-self: stretch;
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow-y: scroll;
    .title {
      margin: 1rem;
      align-self: center;
    }
  }
}
.messages {
  flex: 3;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  flex-direction: column;
}
.vue-popover {
  // width: auto !important;
  left: 40% !important;
  // top: auto !important;
}
</style>
