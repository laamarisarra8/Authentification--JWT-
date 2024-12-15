import {Component} from '@angular/core';
import {RegistrationRequest} from "../../services/models/registration-request";
import {Route, Router} from "@angular/router";
import {AuthenticationService} from "../../services/services/authentication.service";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {


  registerRequest: RegistrationRequest = {email: '', firstname: '', lastname: '', password: ''};
  errorMsg: Array<string> = [];

  constructor(
    private router: Router,
    private authService: AuthenticationService
  ) {
  }


  register() {
    this.errorMsg = [];
    this.authService.resister({
      body: this.registerRequest

    }).subscribe({
        next: () => {
          this.router.navigate((['activate-account']));
        },
      error:(err) => {
          this.errorMsg = err.error.validationErrors;

      }
      }
    )
  }

  login() {
    //if we click on Login we are redirected to the login page
    this.router.navigate(['login']);
  }
}
