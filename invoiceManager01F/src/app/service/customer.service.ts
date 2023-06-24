import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, tap, throwError } from 'rxjs';
import { CustomHttpResponse, CustomerState, Page } from '../interface/app-states';
import { User } from '../interface/user';
import { Stats } from '../interface/stats';
import { Customer } from '../interface/customer';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  private readonly server: string = 'http://localhost:8080';

  constructor(private http: HttpClient) { }

  customers$ = (page: number = 0, size: number = 10) => <Observable<CustomHttpResponse<Page & User & Stats>>>
    this.http.get<CustomHttpResponse<Page & User & Stats>>
      (`${this.server}/customer/list?page=${page}`)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      );

  newCustomer$ = (customer: Customer) => <Observable<CustomHttpResponse<Customer & User>>>
    this.http.post<CustomHttpResponse<Customer & User>>
      (`${this.server}/customer/create`, customer)
      .pipe(
        tap(console.log),
        catchError(this.handleError));

  searchCustomers$ = (name: string = '', page: number = 0) => <Observable<CustomHttpResponse<Page & User>>>
    this.http.get<CustomHttpResponse<Page & User>>
      (`${this.server}/customer/search?name=${name}&page=${page}`).pipe(
        tap(console.log),
        catchError(this.handleError)
      );

  customer$ = (customerId: number) => <Observable<CustomHttpResponse<CustomerState>>>
    this.http.get<CustomHttpResponse<CustomerState>>
      (`${this.server}/customer/get/${customerId}`).pipe(
        tap(console.log),
        catchError(this.handleError)
      );

  update$ = (customer: Customer) => <Observable<CustomHttpResponse<CustomerState>>>
    this.http.put<CustomHttpResponse<CustomerState>>
      (`${this.server}/customer/update`, customer).pipe(
        tap(console.log),
        catchError(this.handleError)
      );

  private handleError(error: HttpErrorResponse): Observable<never> {
    console.log(error);
    let errorMessage: string;
    if (error.error instanceof ErrorEvent) {
      errorMessage = `A client error occurred - ${error.error.message}`;
    } else {
      if (error.error.reason) {
        errorMessage = error.error.reason;
        console.log(errorMessage);
      } else {
        console.log(error.error.reason);
        errorMessage = `An error occurred - Error status ${error.status}`;
      }
    }
    return throwError(() => errorMessage);
  }
}
