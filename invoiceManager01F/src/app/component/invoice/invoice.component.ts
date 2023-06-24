import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { BehaviorSubject, Observable, catchError, map, of, startWith, switchMap } from 'rxjs';
import { DataState } from 'src/app/enum/data-state';
import { CustomHttpResponse } from 'src/app/interface/app-states';
import { Customer } from 'src/app/interface/customer';
import { Invoice } from 'src/app/interface/invoice';
import { State } from 'src/app/interface/state';
import { User } from 'src/app/interface/user';
import { CustomerService } from 'src/app/service/customer.service';
import { jsPDF } from 'jspdf';

@Component({
  selector: 'app-invoice',
  templateUrl: './invoice.component.html',
  styleUrls: ['./invoice.component.css']
})
export class InvoiceComponent implements OnInit {
  invoiceState$: Observable<State<CustomHttpResponse<Customer & Invoice & User>>>;
  private dataSubject = new BehaviorSubject<CustomHttpResponse<Customer & Invoice & User>>(null);
  private isLoadingSubject = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoadingSubject.asObservable();
  readonly DataState = DataState;
  private readonly INVOICE_ID: string = 'id';


  constructor(private activatedRoute: ActivatedRoute, private customerService: CustomerService) { }

  ngOnInit(): void {
    this.invoiceState$ = this.activatedRoute.paramMap.pipe(
      switchMap((params: ParamMap) => {
        return this.customerService.invoice$(+params.get(this.INVOICE_ID)).pipe(
          map(response => {
            console.log(response);
            this.dataSubject.next(response);
            console.log(this.dataSubject.value.data);
            return { dataState: DataState.LOADED, appData: response };
          }),
          startWith({ dataState: DataState.LOADING }),
          catchError((error: string) => {
            return of({ dataState: DataState.ERROR, error })
          })
        );
      })
    );
  }

  exportAsPDF(): void {
    const filename = `invoice-${this.dataSubject.value.data['invoice'].invoiceNumber}.pdf`;
    const doc = new jsPDF();
    doc.html(document.getElementById('invoice'), {
      margin: 5, windowWidth: 1000, width: 200,
      callback: (invoice) => invoice.save(filename)
    });
  }



}