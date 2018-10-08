const version = '1.0.0';
const log = console.log.bind(console)
function initTermPicker() {
  // 选择支付方式
  $('.term-picker .term-item').on('click', function () {
    $('.term-picker .term-item').removeClass('choosen')
    $(this).addClass('choosen')
  })
}

function initCardPicker() {
  // 打开选择银行卡modal
  $('.term-picker .logo-more').on('click', function () {
    $('.card-picker-container').fadeIn()
  })

  $('.card-picker .logo-go-back').on('click', function () {
    $('.card-picker-container').fadeOut()
  })

  $('.card-picker li.bank-card-item').on('click', function () {
    $('.card-picker li.bank-card-item').removeClass('choosen')
    $(this).addClass('choosen')
    $('.term-picker .term-item .bank-card').attr("value", $(this).find(".card-number").attr("value"));
    $('.term-picker .term-item .bank-card').text($(this).find(".bank-name").text() + " " + $(this).find(".card-number").text());
  })

  $('.card-picker .add-new-card').click(function () {
      var contextPath = window.location.pathname.split("/")[1];
      console.log(contextPath);
      var code = getUrlParam("code");

      window.location.href = 'card_cashier?code='+code + "&channel=" + $('.term-picker .term-item.choosen .text').attr("value");
  })
}

function initSubmit() {
    $('#normal-submit').click(function () {
        var contextPath = window.location.pathname.split("/")[1];
        console.log(contextPath);
        var code = getUrlParam("code");
        var url = "/" + contextPath + "/cash/cashier/has_repaying?code="+code ;
        var params={"code":code,"channel":$('.term-picker .term-item.choosen .text').attr("value"),"bankNo":$('.term-picker .term-item.choosen .bank-card').attr("value")}
        //查询是否有正在还款中订单
        $.get(url,function(response){
            var code = response.code;
            if (code != 0) {
                //交易失效 , 直接提交交易 , 由交易接口返回正常错误信息
                /** json 数据提交*/
                initSubmitInside(contextPath ,  params);
            } else {
                var data = response.data;
                if(data){
                    //存在扣款中订单 提示
                    var flag = confirm("是否确认删除!");
                    if(flag){
                        /** json 数据提交*/
                        initSubmitInside(contextPath ,  params);
                    }
                }else{
                    //存在扣款中订单 , 直接提交还款
                    /** json 数据提交*/
                    initSubmitInside(contextPath ,  params);
                }

            }
        });

    })
}

function initSubmitInside( contextPath ,  params){
    $.post("/" + contextPath + "/cash/cashier/submit",params,function(response){
        var code = response.code;
        var message = response.message;
        if (code != 0) {
            window.location.href = 'failed?code='+code + "&message=" + message;
        } else {
            var entities = response.data.entities;
            var state = entities.state;
            if (state == false) {
                code = -123456789;
                message = 88888888;
                window.location.href = 'failed?code='+code + "&message=" + message;
            } else {
                window.location.href = entities.result;
            }
        }
    },"json");
}

function initCardSubmit() {
    $('#card-submit').click(function () {
        /** json 数据提交*/
        var contextPath = window.location.pathname.split("/")[1];
        console.log(contextPath);
        var code = getUrlParam("code");
        var channel = getUrlParam("channel");
        var params={"code":code,"channel":channel,"userName":$('#user-name').val(),"idCard":$('#id-card').val(),"bankNo":$('#bank-no').val(),"mobile":$('#mobile').val()}
        $.post("/" + contextPath + "/cash/card_cashier/submit",params,function(response){
            var code = response.code;
            var message = response.message;
            if (code != 0) {
                window.location.href = 'failed?code='+code + "&message=" + message;
            } else {
                var entities = response.data.entities;
                var state = entities.state;
                if (state == false) {
                    code = -123456789;
                    message = 88888888;
                    window.location.href = 'failed?code='+code + "&message=" + message;
                } else {
                    window.location.href = entities.result;
                }
            }
        },"json");
    })
}

function payAgain() {
    $('#go-pay-again').click(function () {
        window.location.href="http://www.tiantianyouqian.com/repay_finish";
    })
}

function getUrlParam (name) {
    var r = window.location.search.substr(1).match(new RegExp("(^|&)" + name + "=([^&]*)(&|$)"));
    if (r!=null) {
        return unescape(r[2]);
    }
    return null;
}

function main() {
  log('ready')
  initTermPicker()
  initCardPicker()
    initSubmit()
    initCardSubmit()
    payAgain()
}

$(document).ready(main)
// main()