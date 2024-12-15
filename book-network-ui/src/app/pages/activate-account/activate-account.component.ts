import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {AuthenticationService} from "../../services/services/authentication.service";

@Component({
  selector: 'app-activate-account',
  templateUrl: './activate-account.component.html',
  styleUrls: ['./activate-account.component.scss']
})
export class ActivateAccountComponent {


  message: string = ""; // it will hold any kind of Msg error or others
  isOkay: boolean= true; // to check if the account was activated or not
  submitted: boolean = false; //


  constructor(
    private router :Router,
    private authService: AuthenticationService

  ) {
  }

  onCodeCompleted(token: string) {
    this.confirmaccount(token);

  }

  redirectToLogin() {
    this.router.navigate(['login']);
  }

  private confirmaccount(token: string) {
    this.authService.confirm(
      {
        token
      }).subscribe(
      {
        next:() =>{
          this.message = 'Your Account Has been Successfuly Activated .\n Now you can Login '
          this.submitted= true;
          this.isOkay= true;
        },
        error:() =>{
          this.message = 'Token Has Been expired or invalid '
          this.submitted= true;
          this.isOkay= false;

        }
      }
    )



  }
}
