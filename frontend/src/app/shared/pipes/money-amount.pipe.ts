import { Pipe, PipeTransform } from '@angular/core';
import { Money } from '../../core/models/api.models';

@Pipe({ name: 'moneyAmount', standalone: true })
export class MoneyAmountPipe implements PipeTransform {
  transform(money: Money): number {
    return money.currency === 'INR' ? money.amountUnits / 100 : money.amountUnits;
  }
}
