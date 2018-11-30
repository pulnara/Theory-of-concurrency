var fs = require('fs');
var file = require('file');

const {
    performance,
    PerformanceObserver
} = require('perf_hooks');


function asynchronous(root) {
    var t0 = performance.now();
    var lines_total = 0;
    var remaining = 0;
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
                    var t1 = performance.now();
                    console.log(lines_total);
                    console.log("Asynchronous: " + (t1 - t0) + " milliseconds.");
                }
            }).on('error', function(err) {
                console.error(err);
            });

        })
    })
}


function count_lines(all_files, i, callback) {
    var count = 0;
    fs.createReadStream(all_files[i]).on('data', function(chunk) {
        count += chunk.toString('utf8')
            .split(/\r\n|[\n\r\u0085\u2028\u2029]/g)
            .length-1;
    }).on('end', function() {
        // console.log(f, count);
        if (i !== 0) {
            count_lines(all_files, i - 1, callback)
        }
        callback(count, i);
    }).on('error', function(err) {
        console.error(err);
    });

}

function synchronous(root) {
    var t0 = performance.now();
    var lines_total = 0;
    var all_files = [];
    file.walkSync(root, function syncCallback(dirPath, dirs, files) {
        files.forEach(function(f) {
            all_files.push(file.path.join(dirPath, f))
        })
    });

    count_lines(all_files, all_files.length-1, function(counter, i) {
        lines_total += counter;
        if (i === 0) {
            var t1 = performance.now();
            console.log(lines_total);
            console.log("Synchronous: " + (t1 - t0) + " milliseconds.");
        }
    });
}

synchronous("./PAM08");
asynchronous("./PAM08");
