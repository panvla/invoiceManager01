import { NgModule } from "@angular/core";
import { SharedModule } from "src/app/shared/shared.module";
import { InvoiceDetailComponent } from "./invoice-detail/invoice-detail.component";
import { InvoicesComponent } from "./invoices/invoices.component";
import { NewinvoiceComponent } from "./newinvoice/newinvoice.component";
import { InvoiceRoutingModule } from "./invoice-routing.module";
import { NavbarModule } from "../navbar/navbar.module";




@NgModule({
    declarations: [
        InvoiceDetailComponent, NewinvoiceComponent, InvoicesComponent
    ],
    imports: [SharedModule, InvoiceRoutingModule, NavbarModule]
})
export class InvoiceModule { }