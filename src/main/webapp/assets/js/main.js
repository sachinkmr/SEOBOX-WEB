var start = 0;
var poll = true;
var bttn = $('#single-site-form .sbmt');


$(document).ready(function () {
    var timer;
    $('#fileURL').fileupload({
        dataType: 'json',
        url: 'FileUploadServlet',
        type: 'POST',
        singleFileUploads: true,
        autoUpload: true,
        replaceFileInput: false,
        progress: function (e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            $('#orangeBar').css(
                    'width',
                    progress + '%'
                    );
        },
        add: function (e, data) {
            $('#bar').removeClass('hidden');
            $('#bar').html("<div class='progress' ><div id='orangeBar' class='progress-bar progress-bar-animated progress-bar-info bg-info progress-bar-striped' role='progressbar' aria-valuenow='0' aria-valuemin='0' aria-valuemax='100' style='width: 0%;'></div></div>");
            data.context = $('<p/>').text('Uploading...').appendTo("#orangeBar");
            bttn.addClass('disabled');
            data.submit();
        },
        done: function (e, data) {
            data.context.text('Upload Finished');
            $('#CrawlerConfigFile').val(data.result[0].path);
            bttn.removeClass('disabled');
        },
        fail: function (e, data) {
            $('#orangeBar').removeClass('progress-bar-info');
            $('#orangeBar').removeClass('bg-info');
            $('#orangeBar').addClass('bg-danger');
            $('#orangeBar').addClass('progress-bar-danger');
            data.context.text(data.errorThrown);
            bttn.removeClass('disabled');
        }
    });

    $('#single-site-form')
            .find('#fileURL')
            .change(function (e) {
                $('#single-site-form').bootstrapValidator('revalidateField', 'fileURL');
            }).end().bootstrapValidator({
        // container: '.messages',
        feedbackIcons: {
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            siteURL: {
                validators: {
                    notEmpty: {
                        message: 'URL is required and cannot be empty'
                    },
                    uri: {
                        message: 'URL is not valid'
                    }
                }
            },
            username: {
                validators: {
                    notEmpty: {
                        message: 'Username is required'
                    }
                }
            },
            password: {
                validators: {
                    notEmpty: {
                        message: 'Password is required'
                    }
                }
            },
//            email: {
//                validators: {
//                    notEmpty: {
//                        message: 'Email is required'
//                    }
//                }
//            },
            fileURL: {
                validators: {
                    file: {
                        extension: 'properties',
                        message: 'Please select a java properties file'
                    }
                }
            },
        }
    }).on('success.form.bv', function (e) {
        e.preventDefault();
        e.stopImmediatePropagation();
        if ($('#single-site-form').hasClass('submitting'))
            return;
        $('#single-site-form').addClass('submitting');
        $("#mask img").removeClass("hidden");
        $('#mask').removeClass('hidden');
        $('#mask').css('display', " block");
        $('#mask').css('min-height', $('#single-site-form>div').height() + 100);
        $('#mask').css('width', ($('#sachin').width()));
        $('#resultsData').empty();
        $("#mask #bar-mask").removeClass('hidden');
        var $form = $('#single-site-form');
        $.ajax({
            url: $form.attr('action'),
            type: 'POST',
            cache: false,
            data: $form.serialize(),
            success: function (result) {
                var poll = false;
                $('#CrawlerConfigFile').val("");
                $('#single-site-form').trigger('reset');
                $('#mask').css('opacity', '0.9');
                var msg;
                $("#mask #info #status").html("<b>Status: </b>" + result.status);
                $("#mask #info #type").val(result.status);
                $("#mask #info #buildID").val(result.buildID);
                if (result.status == "Executing") {
                    msg = "You can wait until its complete or follow this <a target='_blank' href='" + result.url + "'>link</a> for detail information. ";
                    $("#mask #info #buildID").val(result.buildID);
                    $("#mask #info #buildUrl").val(result.url);
                    poll = true;
                }
                if (result.status == "inQueue") {
                    msg = "<br/><b>Reason: </b>" + result.reason + "<br/><b>Message: </b>Another site is running the test suite already. Your suite will run after suite is complete. You can wait until all validations are complete or follow this <a target='_blank' href='" + result.builds + "'>link</a> for detail information. ";
                    $("#mask #info #queueUrl").val(result.url);
                    poll = true;
                }
                $("#mask #info #msgs p").html("");
                $("#mask #info p").append(msg);
                if (poll) {
                    timer = setInterval(ajax_call, 4000);
                }

            }
        });
    });

    // polling
    var ajax_call = function () {
        var pollingData = {
            type: $('#info #type').val(),
            buildID: $('#info #buildID').val(),
            queueUrl: $('#info #queueUrl').val(),
            buildUrl: $('#info #buildUrl').val(),
            start: $('#mask #info #start').val()
        }
        $.ajax({
            url: 'Polling',
            type: 'POST',
            cache: false,
            data: pollingData,
            success: function (result) {
                var msg = "";
                $("#mask #info #status").html("<b>Status: </b>" + result.status);
                $("#mask #info #type").val(result.status);
                $("#mask #info #buildID").val(result.buildID);
                if (!result.polling) {
                    clearInterval(timer);
                }
                if (result.status == "Complete" || !result.polling) {
                    clearInterval(timer);
                    poll = false;
                    msg = "Test Suite is complete. View <a target='_blank' href='HistoryPage'>report</a>. ";
                    $("#mask #info #buildUrl").val(result.url);
                    $("#mask img").addClass("hidden");
                    $("#mask h3").html("Completed");
//                    $("#resultsData").html(result.info);
                    $("#mask #bar-mask").addClass('hidden');
                }
                if (result.status == "inQueue") {
                    msg = "<br/><b>Reason: </b>" + result.reason + "Another site is running the test suite already. Your suite will run after suite is complete. You can wait until all validations are complete or follow this <a target='_blank' href='" + result.builds + "'>link</a> for detail information. ";
                    $("#mask #info #queueUrl").val(result.url);
                }
                if (result.status == "Executing") {
                    msg = "You can wait until its complete or follow this <a target='_blank' href='" + result.url + "'>link</a> for detail information. ";
                    $("#mask #info #buildID").val(result.buildID);
                    $("#mask #info #buildUrl").val(result.url);
                    $("#resultsData").append(result.info);
                    $('#info #start').val(result.length);
                }
                $("#mask #info #msgs").html("<b>Message: </b>" + msg);
            }
        });

    };
    // cleaning reports
    cleanReport();
});


function cleanReport() {
    $.ajax({
        type: "GET",
        url: "CleanReports",
        dataType: "json"
    });
}
$('#setAuthentication').click(function () {
    if ($('#setAuthentication').is(':checked')) {
        $('#username').removeAttr('disabled');
        $('#password').removeAttr('disabled');
    } else {
        $('#username').attr('disabled', 'disabled');
        $('#password').attr('disabled', 'disabled');
    }
});


