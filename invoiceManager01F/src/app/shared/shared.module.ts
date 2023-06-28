import { NgModule } from '@angular/core';


import { FormsModule } from '@angular/forms';
import { ExtractArrayValue } from '../pipes/extractvalue.pipe';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@NgModule({
    declarations: [ExtractArrayValue],
    imports: [RouterModule, CommonModule, FormsModule],
    exports: [RouterModule, CommonModule, FormsModule, ExtractArrayValue],
})
export class SharedModule { }