import dialog from './detail.html!text';
import dimension from '../../dimension/dimension'
import './detail.css!'
import $ from 'components/jquery';
import 'bootstrap';
import prompt_dialog from '../../utils/dialog/prompt/prompt';

var submit_method = "PUT";
var form_config = {};

var inst = function() {
    return {
        btn_edit_form : $('#btn_edit'),
        btn_view_form : $('#btn_view'),
        btn_delete_form : $('#btn_delete'),
        btn_submit : $('#detail_dialog > div > div > div.modal-footer > button.btn.btn-primary'),
        all_input : $('*.form-input'),
        all_view : $('*.view'),
        btn_submit: $('#detail_dialog > div > div > div.modal-footer > button.btn.btn-primary'),
        form : $('#detail_dialog > div > div > div.modal-body > form'),
        title: $('#dialog_title')
    }
};

var edit_form = function(){
    console.log("edit form...");
    inst().title.html('编辑中');
    inst().btn_edit_form.hide();
    inst().btn_delete_form.hide();
    inst().btn_view_form.show();
    inst().btn_submit.show();
    inst().all_input.show();
    inst().all_view.hide();
};

var view_form = function(){
    submit_method = "PUT";
    console.log("view form...");
    inst().title.html($('#service_name').text());
    inst().btn_edit_form.show();
    inst().btn_delete_form.show();
    inst().btn_view_form.hide();
    inst().btn_submit.hide();
    inst().all_input.hide();
    inst().all_view.show();
}

var create_form = function(){
    submit_method = "POST";
    console.log("create form ...");
    inst().title.html('创建服务');
    inst().btn_edit_form.hide();
    inst().btn_delete_form.hide();
    inst().btn_view_form.hide();
    inst().btn_submit.show();
    inst().all_input.show();
    inst().all_view.hide();
}

var fun_submit = function(){
    var formJson = {};
    inst().form.serializeArray().map(function(x){formJson[x.name] = x.value;});
    console.log(submit_method + ' submitting ' + JSON.stringify(formJson));
    $.ajax({
        type: submit_method,
        url: 'http://localhost:8080/service/'+formJson.id,
        dataType:'json',
        contentType:'application/json',
        data: JSON.stringify(formJson),
        success: function(data){
            console.log("成功更新... " + data);
            $('#detail_dialog').modal('hide');
            form_config.onSubmit();
        }
    })
}

var delete_service = function(){
    prompt_dialog.show({
        content: '确定删除？',
        onConfirm: function(){
            var formJson = {};
            inst().form.serializeArray().map(function(x){formJson[x.name] = x.value;});
            console.log('deleting service...');
            $.ajax({
                type: 'DELETE',
                url: 'http://localhost:8080/service/' + formJson.id,
                contentType:'application/json',
                success: function(data){
                    console.log("成功删除... " + data);
                    $('#detail_dialog').modal('hide');
                    form_config.onSubmit();
                }
            });
        },
        onCancel:function(){}
    });
}

export default function init(){
    return {
        init: function(config) {
            form_config = config;
            $('body').append(dialog);
            inst().btn_edit_form.on('click',edit_form);
            inst().btn_view_form.on('click',view_form);
            inst().btn_delete_form.on('click',delete_service);
            dimension().location('#fm_service_location');
            dimension().status('#fm_service_status');
            dimension().type('#fm_service_type');
            dimension().ip('#fm_service_ip');
            view_form();
            inst().btn_submit.on('click',fun_submit);
            $('#detail_dialog').modal('hide');
        },
        show: function(param){
            if(!param.create){
                var service = param.service;

                $('#fm_service_id').val(service.id);

                $('#service_name').html(service.name);
                $('#fm_service_name').val(service.name);

                $('#service_url').html(service.url);
                $('#fm_service_url').val(service.url);

                $('#service_ip').html(service.ip);
                $('#fm_service_ip').select2('val',service.ip);

                $('#service_location').html(service.location);
                $('#fm_service_location').select2('val',service.location);

                $('#service_status').html(service.status);
                $('#fm_service_status').select2('val',service.status);

                $('#service_type').html(service._type);
                $('#fm_service_type').select2('val',service._type);

                $('#service_comment').html(service.comment);
                $('#fm_service_comment').val(service.comment);
                view_form();
            }else{
                create_form();
                $('#fm_service_id').val('');
                $('#fm_service_name').val('');
                $('#fm_service_url').val('');
                $('#fm_service_ip').select2('val','');
                $('#fm_service_location').select2('val','');
                $('#fm_service_status').select2('val','');
                $('#fm_service_type').select2('val','');
                $('#fm_service_comment').val('');
            }
            $('#detail_dialog').modal('show');

        }
    }
}