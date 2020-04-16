import VueRouter from "vue-router";

let routes = [
    {
        path: "/new",
        component: require("./components/PageComponent.vue").default
    },
    {
        path: "/pages",
        component: require("./components/PagesComponet.vue").default
    },
    {
        path: "/addnotifications",
        component: require("./components/AddNotificationComponent.vue").default
    },
    {
        path: "/logging",
        component: require("./components/Logging.vue").default
    },
    {
        path: "/infotext",
        component: require("./components/WarningComponent.vue").default
    },
    {
        path: "/users",
        component: require("./components/ManageUserComponent.vue").default
    }
];

export default new VueRouter({
    routes,
    linkActiveClass: "is-active", // active class for non-exact links.
    linkExactActiveClass: "is-active" // active class for *exact* links.
});
