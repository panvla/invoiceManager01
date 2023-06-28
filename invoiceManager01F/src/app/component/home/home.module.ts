import { NgModule } from "@angular/core";
import { SharedModule } from "src/app/shared/shared.module";
import { HomeComponent } from "./home/home.component";
import { HomeRoutingModule } from "./home-routing.module";
import { NavbarModule } from "../navbar/navbar.module";
import { StatsModule } from "../stats/stats.module";



@NgModule({
    declarations: [
        HomeComponent
    ],
    imports: [SharedModule, HomeRoutingModule, NavbarModule, StatsModule]
})
export class HomeModule { }