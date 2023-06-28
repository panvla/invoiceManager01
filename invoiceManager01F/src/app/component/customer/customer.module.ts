import { NgModule } from "@angular/core";
import { SharedModule } from "src/app/shared/shared.module";
import { CustomerDetailComponent } from "./customer-detail/customer-detail.component";
import { CustomersComponent } from "./customers/customers.component";
import { NewcustomerComponent } from "./newcustomer/newcustomer.component";
import { CustomerRoutingModule } from "./customer-routing.module";
import { NavbarModule } from "../navbar/navbar.module";



@NgModule({
    declarations: [
        CustomerDetailComponent, NewcustomerComponent, CustomersComponent
    ],
    imports: [SharedModule, CustomerRoutingModule, NavbarModule]
})
export class CustomerModule { }