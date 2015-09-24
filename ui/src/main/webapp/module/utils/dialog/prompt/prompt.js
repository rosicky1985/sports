'use strict';
import html from './prompt.html!text';
import $ from 'components/jquery';

var promptDiv = $(html);
promptDiv.hide();

var content = promptDiv.find('div > div > div.modal-body > span');
var btn_confirm = promptDiv.find('div > div > div.modal-footer > button.btn-primary');
var btn_cancel = promptDiv.find('div > div > div.modal-footer > button.btn-default');

var show = function(config){
    content.html(config.content);
    btn_confirm.on('click',function(){config.onConfirm(); promptDiv.modal('hide')});
    btn_cancel.on('click',function(){config.onCancel(); promptDiv.modal('hide')});
    promptDiv.modal('show');
}

var hide = function(){
    promptDiv.modal('hide');
}

export default {
    show: show,
    hide: hide
}