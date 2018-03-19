import moment from 'moment';
import momentDurationFormatSetup from 'moment-duration-format';

function formatDuration(value, unit) {
    return moment.duration(value, unit).format('h:mm:ss');
}

function parseDurationToSeconds(duration) {
    var parts = duration.split(":");
    if (parts.length === 2) {
        return parseInt(parts[0]) * 60 + parseInt(parts[1]);
    } else if (parts.length === 3) {
        return parseInt(parts[0]) * 60 * 60 + parseInt(parts[1]) * 60 + parseInt(parts[2]);
    }
    return parseInt(parts[0]);
}

export {
    formatDuration,
    parseDurationToSeconds
};