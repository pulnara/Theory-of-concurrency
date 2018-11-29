var fs = require('fs');
var file = require('file');
var async = require('async');

const {
    performance,
    PerformanceObserver
} = require('perf_hooks');


function asynchronous(root) {
    var t0 = performance.now();
    var lines_total = 0;
    var remaining = 0;
    async.parallel(
        [function(callback) {
            file.walk(root, function asyncCallback(sth, dirPath, dirs, files) {
                files.forEach(function(f) {
                    var count = 0;
                    remaining += 1;
                    fs.createReadStream(f).on('data', function(chunk) {
                        count += chunk.toString('utf8')
                            .split(/\r\n|[\n\r\u0085\u2028\u2029]/g)
                            .length-1;
                    }).on('end', function() {
                        // console.log(f, count);
                        lines_total += count;
                        remaining -= 1;
                        if (!remaining) {
                            callback();
                        }
                    }).on('error', function(err) {
                        console.error(err);
                    });

                })
            })
        }
        ], function() {
            var t1 = performance.now();
            console.log(lines_total);
            console.log("Asynchronous: " + (t1 - t0) + " milliseconds.");
        }
    )
}


function synchronous(root) {
    var t0 = performance.now();
    var lines_total = 0;
    var remaining = 0;
    file.walkSync(root, function syncCallback(dirPath, dirs, files) {
        files.forEach(function(f) {
            var count = 0;
            remaining += 1;
            fs.createReadStream(file.path.join(dirPath, f)).on('data', function(chunk) {
                count += chunk.toString('utf8')
                    .split(/\r\n|[\n\r\u0085\u2028\u2029]/g)
                    .length-1;
            }).on('end', function() {
                // console.log(f, count);
                lines_total += count;
                remaining -= 1;
                if (!remaining) {
                    var t1 = performance.now();
                    console.log(lines_total);
                    console.log("Synchronous: " + (t1 - t0) + " milliseconds.");
                }

            }).on('error', function(err) {
                console.error(err);
            });

        })
    })
}

asynchronous("./PAM08");
synchronous("./PAM08");
