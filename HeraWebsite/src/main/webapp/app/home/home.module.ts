import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HeraShopSharedModule } from 'app/shared';
import { ALL_HOME_ROUTE, HomeComponent } from './';
import { ProductListModule } from 'app/product-list/product-list.module';
import { OrderListModule } from 'app/order-list/order-list.module';

@NgModule({
    imports: [HeraShopSharedModule, RouterModule.forChild(ALL_HOME_ROUTE), ProductListModule, OrderListModule],
    declarations: [HomeComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class HeraShopHomeModule {}
