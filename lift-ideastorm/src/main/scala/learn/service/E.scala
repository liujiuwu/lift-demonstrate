package learn.service

import learn.model.Account

object E {
  def account(accountId: String) = Account.find(accountId)
    
}
