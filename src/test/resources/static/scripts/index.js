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
  })

  $('.card-picker .add-new-card').click(function () {
    window.location.href = 'card_adder.html'
  })
}
function initSubmit() {
  $('.btn-submit').click(function () {
    log('数据提交了')
  })
}

function main() {
  log('ready')
  initTermPicker()
  initCardPicker()
  initSubmit()
}

$(document).ready(main)
// main()