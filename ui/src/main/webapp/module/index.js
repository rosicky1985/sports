'use strict';
import $ from 'components/jquery';
import 'bootstrap';
import 'bootstrap/css/bootstrap.css!';
import 'module/utils/date';


var now = new Date();
var now_str = now.Format("yyyyMMddhhmmss");
var calendar_div = $('#calendar');

function render_calendar(){
    calendar_div.html('');
    function* date_range(){
        for (var i = 10 ; i >=0; i --) {
            var now = new Date()
            now.setDate(now.getDate()-i)
            yield now.Format("yyyy-MM-dd")
        }
    }

    var range = date_range();
    for(var d of range){
        var span = $(`<div class="date">${d}</div>`);
        calendar_div.append(span);
        if(d == now.Format('yyyy-MM-dd')){
            span.addClass('active');
            span.addClass('today');
        }
    }
};

render_calendar();

var table_frame =
`<table class="table table-bordered">
     <thead>
     <tr>
         <th>股票</th>
         <th>价格</th>
         <th>涨幅</th>
         <th>时间</th>
     </tr>
     </thead>
     <tbody>

     </tbody>
 </table>`;


$.ajax({
    type: "GET",
    url: `http://localhost:8080/stock?after=${now_str}`,
    success: function(data){
        var data_tbody = $('#data');
        console.log("成功更新... " + data);
        // group by day
        var by_day = {};
        for (var l of data) {
            var day = l.time.substr(0,10)
            var time = l.time.replace(' ','_')
            // group by time
            if( ! (day in by_day)) { by_day[day] = {} }
            if(! (time in by_day[day])) {by_day[day][time] = new Array()}
            by_day[day][time].push(l)
        }
        console.log(by_day)

        // render
        var data_div = $('#data');
        var ds = $('div.date');
        var click = function(div){
            $(calendar_div.find('.active')).removeClass('active');
            div.addClass('active');
            var day = div.text();
            if(!(day in by_day)) {
                data_div.html('<h3>当日无数据</h3>');
            }else{
                data_div.html('');
                var dom_table = $(table_frame);
                data_div.append(dom_table);
                var dom_tbody = $(dom_table.find('tbody'));
                var odd = true;
                for(var byTime in by_day[day]){
                    var lines = by_day[day][byTime];
                    lines.sort((a,b)=>a.name-b.name);
                    for(var line of lines){
                        var tr = $('<tr>');
                        if(odd) {tr.addClass('odd');} else {tr.addClass('even');}
                        dom_tbody.append(tr);
                        tr.append($(`<td>${line.name}</td>`));
                        tr.append($(`<td class="number">${line.price}</td>`));
                        var float = $(`<td class="number">${line.float}%</td>`);
                        tr.append(float);
                        if(line.float > 0) {float.addClass('up')}else{float.addClass('down')}
                        tr.append($(`<td>${line.time}</td>`));
                    }
                    odd = !odd;
                }
            }
        }
        for(var i = 0; i < ds.length; i ++){
            var d = $(ds[i]);
            var day = d.text() ;
            if(!(day in by_day)){
                d.addClass('no_data');
            }
            d.on('click', e => click($(e.target)));
            if(day == now.Format('yyyy-MM-dd')){
                click(d);
            }
        }
    }
});
