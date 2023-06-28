import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';
import { CoreModule } from './core/core.module';
import { AuthModule } from './component/auth/auth.module';
import { CustomerModule } from './component/customer/customer.module';
import { HomeModule } from './component/home/home.module';
import { InvoiceModule } from './component/invoice/invoice.module';

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [BrowserModule, HttpClientModule, CoreModule, AuthModule, CustomerModule, HomeModule, InvoiceModule, AppRoutingModule],
  bootstrap: [AppComponent],
})
export class AppModule { }
