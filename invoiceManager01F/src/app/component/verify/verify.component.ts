import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { BehaviorSubject, Observable, catchError, map, of, startWith, switchMap } from 'rxjs';
import { DataState } from 'src/app/enum/data-state';
import { AccountType, VerifyState } from 'src/app/interface/app-states';
import { User } from 'src/app/interface/user';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-verify',
  templateUrl: './verify.component.html',
  styleUrls: ['./verify.component.css']
})
export class VerifyComponent implements OnInit {
  verifyState$: Observable<VerifyState>;
  private userSubject = new BehaviorSubject<User>(null);
  user$ = this.userSubject.asObservable();
  private isLoadingSubject = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoadingSubject.asObservable();
  readonly DataState = DataState;

  constructor(private activatedRoute: ActivatedRoute, private userService: UserService) { }


  ngOnInit(): void {
    this.verifyState$ = this.activatedRoute.paramMap.pipe(
      switchMap((params: ParamMap) => {
        console.log(this.activatedRoute);
        const type: AccountType = this.getAccountType(window.location.href);
        return this.userService.verify$(params.get('key'), type).pipe(
          map(response => {
            console.log(response);
            type === 'password' ? this.userSubject.next(response.data.user) : null;
            return { type, title: 'Verified!', dataState: DataState.LOADED, message: response.message, verifySuccess: true };
          }),
          startWith({ title: 'Verifying...', dataState: DataState.LOADING, message: 'Please wait while we verify the information', verifySuccess: false }),
          catchError((error: string) => {
            return of({ title: 'An error occured', dataState: DataState.ERROR, error, message: error, verifySuccess: false })
          })
        );
      })
    );
  }

  renewPassword(resetPasswordform: NgForm): void {
    this.isLoadingSubject.next(true);
    this.verifyState$ = this.userService.renewPassword$({
      userId: this.userSubject.value.id,
      password: resetPasswordform.value.password,
      confirmPassword: resetPasswordform.value.confirmPassword
    }).pipe(
      map(response => {
        console.log(response);
        this.isLoadingSubject.next(false);
        return { type: 'account' as AccountType, title: 'Success', dataState: DataState.LOADED, message: response.message, verifySuccess: true };
      }),
      startWith({ type: 'password' as AccountType, title: 'Verified!', dataState: DataState.LOADED, verifySuccess: false }),
      catchError((error: string) => {
        this.isLoadingSubject.next(false);
        return of({ type: 'password' as AccountType, title: 'Verified!', dataState: DataState.ERROR, message: error, verifySuccess: true })
      })
    );
  }

  private getAccountType(url: string): AccountType {
    return url.includes('password') ? 'password' : 'account';
  }
}
