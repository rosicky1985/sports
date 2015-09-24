import $ from 'components/jquery';
import select2 from 'select2';

function select2_custom(config) {
    $(config.domid).select2({
        allowClear: true,
        placeholder: config.placeholder,
        dropdownAutoWidth: 'true',
        initSelection: function (element, callback) {
            var id = $(element).val();
            callback({id:id,text:id});
        },
        ajax: {
            url: config.url ,
            dataType: 'json',
            type: 'GET',
            data: function(term){
                return {l:term.term}
            },
            processResults: function(data){
                return {
                    results : $.map(data,function(item){
                        return {
                            id: item,
                            text: item
                        }
                    })
                }
            }
        },
        createSearchChoice:function(term, data) {
            if ( $(data).filter( function() {
              return this.text.localeCompare(term)===0;
            }).length===0) {
              return {id:term, text:term};
            }
        }
    });
}

export default function init() {
    return {
        location: function(domid){
            select2_custom({
                domid:domid,
                placeholder:"请输入位置",
                url:'http://localhost:8080/service/location'
            });
        },

        status : function(domid){
            select2_custom({
                domid:domid,
                placeholder:"请输入状态",
                url:'http://localhost:8080/service/status'
            });
        },

        ip : function(domid){
            select2_custom({
                domid:domid,
                placeholder:"请输入服务器ip",
                url:'http://localhost:8080/service/ip'
            });
        },
        type: function(domid){
            select2_custom({
                domid:domid,
                placeholder:"请输入类型",
                url:'http://localhost:8080/service/type'
            });
        }
    }
}