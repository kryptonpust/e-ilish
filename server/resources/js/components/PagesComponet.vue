<template>
  <div class="container">
    <div class="box">
      <div class="field">
        <label class="title" style="color:red">View/Edit Pages</label>
      </div>
      <div class="box">
        <table class="table is-fullwidth is-hoverable is-striped is-centered">
          <thead>
            <tr>
              <th>Arrow</th>
              <th>Title</th>
              <th>Actions</th>
            </tr>
          </thead>
          <draggable element="tbody" v-model="data" @end="onchange">
            <!-- v-model="com.committees" @end="onchange(com.committees)"> -->
            <tr v-for="(d,index) in data" v-bind:key="index">
              <td>
                <i class="fas fa-arrows-alt"></i>
              </td>
              <td>
                <div :style="{'color': d.titlecolor}" class="title">{{d.title}}</div>
              </td>
              <td>
                <a class="button is-warning" @click="show(d)">Show</a>
                <a class="button is-danger" @click="del(d.id)">Delete</a>
              </td>
            </tr>
            <!-- <a slot="footer" class="button is-success">Update</a> -->
          </draggable>
          <!-- </tbody> -->
        </table>
      </div>

      <div class="modal is-active" v-if="showmodal">
        <div class="modal-background"></div>
        <div class="modal-content">
          <div class="box">
            <div class="field">
              <label class="label">Title</label>
              <div class="control parent">
                <input
                  class="input"
                  type="text"
                  :disabled="!edit"
                  placeholder="Title"
                  :style="{'color': editdata.titlecolor}"
                  v-model="editdata.title"
                  autofocus
                >
                <Swatches
                  class="childcolor"
                  show-fallback
                  :disabled="!edit"
                  v-model="editdata.titlecolor"
                  colors="material-light"
                  popover-to="left"
                ></Swatches>
              </div>
            </div>
            <div class="field">
              <label class="label">Details</label>
              <div class="control">
                <editor :init="tinyinit" :disabled="!edit" v-model="editdata.html"/>
                <a
                  class="button is-warning"
                  style="margin: 6px"
                  @click="editactive"
                  v-if="!edit"
                >Edit</a>
                <div v-else>
                  <a class="button is-success" style="margin: 6px" @click="clicksave()">Save</a>
                  <a class="button is-danger" style="margin: 6px" @click="canclemodal">Cancle</a>
                </div>
              </div>
            </div>
          </div>
        </div>
        <button class="modal-close is-large" aria-label="close" @click="canclemodal"></button>
      </div>
    </div>
  </div>
</template>
<script>
import Editor from "@tinymce/tinymce-vue";
import draggable from "vuedraggable";
import * as notihelper from "../NotifiactionHandler.js";
import Swatches from "vue-swatches";
import "vue-swatches/dist/vue-swatches.min.css";
export default {
  components: {
    editor: Editor,
    draggable,
    Swatches
  },
  created() {
    this.fetchdata();
  },
  data() {
    return {
      showmodal: false,
      data: [],
      editdata: null,
      dragdata: null,
      edit: false,
      myPlugins: "link paste quickbars autoresize code image imagetools",
      tinyinit: {
        skin: "oxide",
        plugins: [
          "advlist anchor autolink autoresize charmap code codesample directionality emoticons fullpage fullscreen hr image importcss insertdatetime legacyoutput link" +
            " lists nonbreaking noneditable pagebreak paste preview quickbars save searchreplace spellchecker" +
            " tabfocus table template textpattern" +
            " toc visualblocks visualchars wordcount"
        ],
        menubar: "edit insert view format table tools",
        toolbar:
          "insertfile undo redo | fontsizeselect | bold italic font | forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist | link image",
        toolbar_drawer: "floating",
        templates: [
          {
            title: "Basic Template",
            description: "Basic Template",
            content: "<h1>Hello from Template</h1>"
          }
        ],
        style_formats: [
          {
            title: "Image Left",
            selector: "img",
            styles: {
              float: "left",
              margin: "0 10px 0 10px"
            }
          },
          {
            title: "Image Right",
            selector: "img",
            styles: {
              float: "right",
              margin: "0 10px 0 10px"
            }
          }
        ],
        branding: false,
        min_height: 500,
        images_upload_handler: function(blobInfo, success, failure) {
          //console.log(blobInfo);
          var formData = new FormData();
          // formData.append=('image',blobInfo);
          formData.append("image", blobInfo.blob());
          axios
            .post("/api/imageupload", formData, {
              headers: { "Content-Type": "multipart/form-data" }
            })
            .then(data => {
              success(data.data.location);
              //console.log(data);
            })
            .catch(error => {
              failure(error);
              //console.log(error);
            });
        }
      }
    };
  },
  methods: {
    onchange() {
      var index = [];
      this.data.forEach(function(element) {
        index.push(element.id);
      });
      axios
        .post(
          "/api/updatepageindex",
          { index },
          { headers: { "content-type": "application/json" } }
        )
        .then(data => {
          this.$root.$emit("update");
          notihelper.notification.call(this, data);
        })
        .catch(err => {
          alert("Error Occured(" + err + ")");
          notihelper.noti_error.call(this, err);
        });
    },
    canclemodal() {
      this.edit = false;
      this.showmodal = false;
    },
    show(data) {
      this.showmodal = true;
      this.editdata = data;
    },
    editactive() {
      this.edit = true;
    },
    fetchdata() {
      axios
        .get("/api/data")
        .then(data => {
          this.data = data.data.data;
        })
        .catch(err => {
          var result = data.data;
          notihelper.noti_error.call(this, err);
        });
    },
    clicksave() {
      //console.log(this.data);
      if (this.editdata.titlecolor.length >= 7) {
        tinymce.activeEditor.uploadImages(
          function(success) {
            axios
              .post("/api/dataedit", this.editdata, {
                headers: { "content-type": "application/json" }
              })
              .then(data => {
                this.$root.$emit("update");
                notihelper.notification.call(this, data);
                this.edit = false;
                this.fetchdata();
                this.showmodal = false;
              })
              .catch(error => {
                notihelper.noti_error(this, error);
              });
          }.bind(this)
        );
      } else {
        notihelper.noti_warn.call(this, "Color must be 6 digit String");
      }
    },
    del(i) {
      axios
        .delete("/api/datadelete?id=" + i, {
          headers: { "content-type": "application/json" }
        })
        .then(data => {
          this.$root.$emit("update");
          notihelper.notification.call(this, data);
          this.fetchdata();
        })
        .catch(error => {
          notihelper.noti_error(this, error);
          console.log(error);
        });
    }
  }
};
</script>


<style lang="scss" scoped>
.modal-content {
  width: calc(100vw - 20%) !important;
}
th,
td {
  text-align: center;
}
.parent {
  display: flex;
  align-items: center;
  justify-content: space-around;
  .childcolor {
    margin: 2px;
    padding-top: 5px;
  }
}
</style>
