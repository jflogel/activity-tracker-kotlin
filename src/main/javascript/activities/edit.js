import {formatDuration, parseDurationToSeconds} from '../utilities/duration-helper';
import moment from 'moment';

const ActivitiesEdit = { template:
    `<div>
        <ul v-if="errors" class="list-unstyled alert alert-danger">
            <li v-for="error in errors">{{error}}</li>
        </ul>
        <form class="form-horizontal" v-on:submit.prevent v-bind:action="model.id ? '/activities/edit/' + model.id : '/activities/add'" method='POST'>
            <div class="form-group">
                <label class="control-label col-sm-1" for="date">Date:</label>
                <div class="col-sm-3">
                    <input class="form-control" id='date' name='date' type='datetime-local' placeholder='mm/dd/yyyy, hh:mm AM' v-model="model.date">
                </div>
            </div>
            <div class="form-group">
                <label class="control-label col-sm-1" for='activity'>Activity:</label>
                <div class="col-sm-3">
                    <select class="form-control col-sm-1" id='activity' v-model="model.activity" name='activity'>
                        <option v-for="activity in activities" v-bind:value="activity.description">{{activity.description}}</option>
                    </select>
                </div>
            </div>
            <div class="form-group">
                <label class="control-label col-sm-1" for='duration'>Duration:</label>
                <div class="col-sm-3">
                    <input class="form-control col-sm-1" id='duration' name='duration' min='0' type='string' placeholder='hh:mm:ss' v-model="model.time_duration">
                </div>
            </div>
            <div class="form-group">
                <label class="control-label col-sm-1" for='distance'>Distance:</label>
                <div class="col-sm-3">
                    <input class="form-control col-sm-1" id='distance' name='distance' type='text' v-model="model.distance">
                </div>
                <div class="col-sm-1">
                    <select class="form-control col-sm-1" v-model="model.distance_units" name='distance_units'>
                        <option value='miles'>miles</option>
                        <option value='meters'>meters</option>
                        <option value='yards'>yards</option>
                    </select>
                </div>
            </div>
            <div class="form-group">
                <button class="btn btn-default col-sm-1 col-sm-offset-1" type='submit' v-on:click="saveData">Save</button>
                <router-link v-bind:to="createCancelLink(model)" class="btn btn-default col-sm-1">Cancel</router-link>
            </div>
        </form>
    </div>`,
    data: function() {
        return {
            errors: null,
            activities: [],
            model: {
                activity: 'Running',
                date: this.defaultDate(),
                distance_units: 'miles'
            }
        };
    },
    created: function() {
        this.fetchData();
    },
    methods: {
        formatDate: function(date) {
            return moment.unix(date).format('YYYY-MM-DDTHH:mm')
        },
        formatDuration: formatDuration,
        defaultDate: function() {
            return moment().format('YYYY-MM-DDT00:00');
        },
        fetchData: function () {
           const activity = this.$route.params.id || '';
           var xhr = new XMLHttpRequest()
           var self = this
           xhr.open('GET', '/activity/edit?activity=' + activity)
           xhr.onload = function () {
                const json = JSON.parse(xhr.responseText);
                self.activities = json.activities;
                if (json.model) {
                    self.model = Object.assign({}, json.model, {
                        date: self.formatDate(json.model.date),
                        time_duration: self.formatDuration(json.model.time_duration, json.model.time_duration_units),
                    });
                }
           }
           xhr.send()
         },
         saveData: function (event) {
            var xhr = new XMLHttpRequest()
            var self = this
            xhr.open('POST', '/activity/save')
            xhr.setRequestHeader("Content-type", "application/json");
            xhr.onload = function () {
                const response = JSON.parse(xhr.responseText);
                if (response.status >= 400) {
                    self.errors = [response.message];
                } else if (response.errors) {
                    self.errors = response.errors;
                } else {
                    self.$router.push('/activities/list?activity=' + self.model.activity.toLowerCase());
                }
            }
            var requestBody = Object.assign({}, this.model, {
                date: moment(this.model.date, 'YYYY-MM-DDTHH:mm').unix(),
                time_duration: parseDurationToSeconds(this.model.time_duration),
                time_duration_units: 'seconds'
            });
            xhr.send(JSON.stringify(requestBody));
         },
         createCancelLink: function (model) {
            if (model.id && model.activity) {
                return '/activities/list?activity=' + model.activity;
            }
            return '/activities/list';
         }
    }
}

export default ActivitiesEdit;