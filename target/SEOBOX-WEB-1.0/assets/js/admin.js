/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
$('#addTemp-form').bootstrapValidator({
// To use feedback icons, ensure that you use Bootstrap v3.1.0 or later    
    feedbackIcons: {
        invalid: 'glyphicon glyphicon-remove',
        validating: 'glyphicon glyphicon-refresh'
    },
    fields: {
        id1: {
            validators: {
                notEmpty: {
                    message: 'Name is required'
                }
            }
        },
        fileURL: {
            validators: {
                file: {
                    extension: 'zip',
                    message: 'Please select a zip file'
                }, notEmpty: {
                    message: 'File is required'
                }
            }
        }
    }
}).on('success.form.bv', function (e) {
    $('.alert').html("");
    $('.alert').addClass('hidden');
    e.preventDefault();
    var $form = $('#addTemp-form');
    $('#status span').html('Uploading.....');
    $('#bar').removeClass("hidden");
    var formData = new FormData(($form)[0]);
    $.ajax({
        url: $form.attr('action'),
        type: 'POST',
        async: false,
        cache: false,
        contentType: false,
        processData: false,
        data: formData,
        success: function (result) {
            $('#addTemp').modal('hide');
            $('#bar').addClass("hidden");
            var error = result.hasError;
            if (error == true) {
                $('.alert').removeClass('hidden');
                $('.alert').html(result.error);
                console.error(result.error);
            } else {
                loadSchemas();
            }
        }
    });    
});

$('#addTemp-button').click(function () {
    $('#addTemp').modal({
        keyboard: false,
        backdrop: 'static'
    });
});


function loadSchemas() {
    $.ajax({
        url: "LoadSchemas",
        dataType: 'json',
        success: function (result) {
            var error = result.hasError;
            if (error == true) {
                $('.alert').removeClass('hidden');
                console.error(result.error);
            } else {
                $('#template-table tbody').html('');
                $.each(result.list, function (key, val) {
                    var a = parseInt(key);
                    $('#template-table tbody').append('<tr><td>' + ++a + '</td><td>' + val + '</td><td><a href="DeleteSchema?name=' + val + '" class="btn btn-danger delete DeleteSchema"><i class="fa fa-trash"></i>&ensp; Delete</a></td></tr>');
                });
            }
        }
    });
}


$(document).ready(function () {
    loadSchemas();

});