const ActivitiesList = {
    template: '<div id="columnchart_values" style="height: 400px;"></div>',
    data: function () {
        return {
            chartActivities: null
        };
    },
    created: function() {
        google.charts.load('current', {packages: ['corechart']});
        google.charts.setOnLoadCallback(this.fetchData);
    },
    methods: {
     fetchData: function () {
       const activity = this.$route.query.activity || '';
       var xhr = new XMLHttpRequest()
       var self = this
       xhr.open('GET', '/data/activities/chart?activity=' + activity)
       xhr.onload = function () {
            var dataTableData = [];
            const json = JSON.parse(xhr.responseText);
            dataTableData.push(json.legendValues);
            json.summaries.forEach(function(summary) {
                dataTableData.push([summary.date, summary.running, summary.core, summary.swimming, summary.weights]);
            });
            console.log(dataTableData);
            self.chartActivities = json;

            var data = google.visualization.arrayToDataTable(dataTableData);
            var view = new google.visualization.DataView(data);

            var options = {
                height: 400,
                legend: { position: 'top', maxLines: 3 },
                bar: { groupWidth: '75%' },
                isStacked: true
            };
            var chart = new google.visualization.ColumnChart(document.getElementById("columnchart_values"));
            chart.draw(view, options);
       }
       xhr.send()
     }
    }
}
const ActivitiesAdd = { template: '<div>bar</div>' }

const routes = [
  { path: '/', component: ActivitiesList },
  { path: '/activities/list', component: ActivitiesList },
  { path: '/activities/add', component: ActivitiesAdd }
]

const router = new VueRouter({
  routes
})

const app = new Vue({
    router,
    methods: {
        classObject: function (val) {
            const routeActivity = this.$route.query.activity;
            if (routeActivity && routeActivity == val) {
                return 'active';
            }
            if ((!routeActivity || routeActivity == '') && !val) {
                return 'active';
            }
            return '';
        }
    }
}).$mount('#app')