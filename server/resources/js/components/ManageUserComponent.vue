<template>
  <div class="container">
    <div class="cloumns">
      <div class="column">
        <div class="box">
          <div class="title">Users</div>
          <div class="spinner" v-if="data==null"></div>
          <div class="box" v-else>
            <div class="table-container">
              <table class="table is-fullwidth is-hoverable">
                <thead>
                  <tr>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(item, index) in data" :key="index">
                    <td>{{item.name}}</td>
                    <td>{{item.email}}</td>
                    <td>
                      <a class="button is-warning" @click="edit(item.id,item.email,item.name)">Edit</a>
                      <a v-if="item.id!=1" class="button is-danger" @click="predel(item.id)">Delete</a>
                    </td>
                  </tr>
                </tbody>
              </table>

              <a v-if="data[0].id==1" class="button is-success is-fullwidth" @click="add">Add New</a>

              <div class="modal is-active" v-if="showmodel">
                <div class="modal-background"></div>
                <div class="modal-content">
                  <div class="box">
                    <div v-if="addmode" class="title">Add New User</div>
                    <div v-else-if="!addmode && !delmode" class="title">Edit User</div>
                    <div class="field" v-if="!delmode">
                      <label class="label">Name</label>
                      <p class="control">
                        <input
                          class="input"
                          type="text"
                          placeholder="Name"
                          v-model="editdata.name"
                          autofocus
                        >
                      </p>
                    </div>
                    <div class="field" v-if="!delmode">
                      <label class="label">Email</label>
                      <p class="control">
                        <input
                          class="input"
                          type="email"
                          placeholder="Email"
                          v-model="editdata.email"
                          autofocus
                        >
                      </p>
                    </div>
                    <div class="field" v-if="!delmode">
                      <label class="label">New Password</label>
                      <p class="control">
                        <input
                          class="input"
                          type="password"
                          placeholder="New Password"
                          v-model="editdata.pass"
                          autofocus
                        >
                      </p>
                    </div>
                    <div v-if="!addmode" class="field">
                      <label class="label">{{delmode? "Confirm":"Current"}} Password</label>
                      <p class="control">
                        <input
                          class="input"
                          type="password"
                          placeholder="Current Password"
                          required
                          v-model="editdata.cpass"
                          autofocus
                        >
                      </p>
                    </div>
                    <div class="feild" v-if="delmode">
                      <button type="submit" class="button is-danger" @click="del">Delete</button>
                    </div>
                    <div class="feild" v-else>
                      <button
                        type="submit"
                        class="button is-success"
                        @click="submit"
                      >{{!addmode ?"Update":"Add"}}</button>
                    </div>
                  </div>
                </div>
                <button class="modal-close is-large" aria-label="close" @click="showmodel = false"></button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as notihelper from "../NotifiactionHandler.js";

export default {
  data() {
    return {
      data: null,
      showmodel: false,
      editdata: { id: null, name: null, email: null, pass: null, cpass: null },
      addmode: false,
      delmode: false
    };
  },
  created() {
    this.fetchusers();
  },
  methods: {
    fetchusers() {
      axios
        .get("/api/getusers")
        .then(res => {
          this.data = res.data;
        })
        .catch(err => {
          notihelper.noti_error.call(this, err);
          //console.log(err);
        });
    },
    add() {
      this.addmode = true;
      this.delmode = false;
      this.showmodel = true;
      this.editdata.email = null;
      this.editdata.name = null;
      this.editdata.pass = null;
    },
    edit(x, y, z) {
      this.addmode = false;
      this.delmode = false;
      this.showmodel = true;
      this.editdata.id = x;
      this.editdata.email = y;
      this.editdata.name = z;
      this.editdata.pass = null;
      this.editdata.cpass = null;
    },
    predel(x) {
      this.addmode = false;
      this.delmode = true;
      this.showmodel = true;
      this.editdata.id = x;
      this.editdata.cpass = null;
    },
    del() {
      if (this.editdata.cpass) {
        axios
          .post("/api/deleteuser", this.editdata, {
            headers: { "content-type": "application/json" }
          })
          .then(data => {
            notihelper.notification.call(this, data);
            this.showmodel = false;
            this.fetchusers();
          })
          .catch(err => {
            notihelper.noti_error.call(this, err);
          });
      } else {
        this.$notify({
          group: "notification",
          type: "warn",
          title: "User Account",
          text: "Please insert Current password"
        });
      }
    },
    submit() {
      if (this.addmode) {
        axios
          .post("/api/addnewuser", this.editdata, {
            headers: { "content-type": "application/json" }
          })
          .then(data => {
            notihelper.notification.call(this, data);

            this.showmodel = false;
            this.fetchusers();
          })
          .catch(err => {
            notihelper.noti_error.call(this, err);
          });
        this.showmodel = false;
        this.addmode = false;
      } else {
        if (this.editdata.cpass) {
          axios
            .post("/api/updateuser", this.editdata, {
              headers: { "content-type": "application/json" }
            })
            .then(data => {
              notihelper.notification.call(this, data);

              this.showmodel = false;
              this.fetchusers();
            })
            .catch(err => {
              notihelper.noti_error.call(this, err);
            });
        } else {
          this.$notify({
            group: "notification",
            type: "warn",
            title: "User Account",
            text: "Please insert Current password"
          });
        }
      }
    }
  }
};
</script>