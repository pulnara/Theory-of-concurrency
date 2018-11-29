var async = require('async');


async.waterfall([task1, task2, task3], function (err, result) {
    console.log('done');
});


function printAsync(s, cb) {
    var delay = Math.floor((Math.random()*1000)+500);
    setTimeout(function() {
        console.log(s);
        if (cb) cb(null, s+1);
    }, delay);
}


function task1(cb) {
    printAsync(1, cb);
}

function task2(arg1, cb) {
    printAsync(arg1, cb);
}

function task3(arg1, cb) {
    printAsync(arg1, cb);
}


