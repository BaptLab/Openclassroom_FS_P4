import {
  ComponentFixture,
  TestBed,
  async,
  ComponentFixtureAutoDetect,
  tick,
  fakeAsync,
  waitForAsync,
} from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { LoginComponent } from './login.component';
import { SessionService } from 'src/app/services/session.service';
import { Observable, of, throwError } from 'rxjs';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { LoginRequest } from '../../interfaces/loginRequest.interface';
import { AuthService } from '../../services/auth.service';
import { HttpClientModule } from '@angular/common/http';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let router: Router;
  let authService: AuthService;
  let sessionService: SessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        SessionService,
        AuthService,
        { provide: ComponentFixtureAutoDetect, useValue: true },
      ],
      imports: [
        RouterTestingModule,
        BrowserAnimationsModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        HttpClientModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    authService = TestBed.inject(AuthService);
    sessionService = TestBed.inject(SessionService);

    component.form.setValue({
      email: '',
      password: '',
    });

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should log the user when credentials are valid', waitForAsync(async () => {
    const loginSpy = jest.spyOn(component['authService'], 'login');
    const logInSpy = jest.spyOn(component['sessionService'], 'logIn');
    const consoleErrorSpy = jest.spyOn(console, 'error'); // Spy on console.error

    component.form.patchValue({
      email: 'yoga@studio.com',
      password: 'test!1234',
    });

    console.log('Form value before submit:', component.form.value);

    const loginComponentElement = fixture.nativeElement;
    const loginFormElement = loginComponentElement.querySelector('form');
    expect(loginFormElement).toBeTruthy();

    await component.submit();
    await fixture.whenStable();
    fixture.detectChanges();

    // Optionally, add a delay if needed
    await new Promise((resolve) => setTimeout(resolve, 100));

    expect(loginSpy).toHaveBeenCalled();
    expect(loginSpy).toHaveBeenCalledWith({
      email: 'yoga@studio.com',
      password: 'test!1234',
    });

    expect(logInSpy).toHaveBeenCalled();
    expect(consoleErrorSpy).not.toHaveBeenCalled();

    expect(sessionService.isLogged).toBeTruthy();

    sessionService.logOut();
  }));

  it('should handle error when submitting invalid credentials', async () => {
    const loginSpy = jest.spyOn(component['authService'], 'login');
    const logInSpy = jest.spyOn(component['sessionService'], 'logIn');
    const consoleErrorSpy = jest.spyOn(console, 'error'); // Spy on console.error

    component.form.patchValue({
      email: 'wrongusrn@com',
      password: 'wrongpwd',
    });

    const loginComponentElement = fixture.nativeElement;
    const loginFormElement = loginComponentElement.querySelector('form');
    expect(loginFormElement).toBeTruthy();

    await component.submit();
    await fixture.whenStable();
    fixture.detectChanges();

    // Optionally, add a delay if needed
    await new Promise((resolve) => setTimeout(resolve, 100));

    expect(loginSpy).toHaveBeenCalled();
    expect(loginSpy).toHaveBeenCalledWith({
      email: 'wrongusrn@com',
      password: 'wrongpwd',
    });

    expect(logInSpy).not.toHaveBeenCalled();
    expect(consoleErrorSpy).toHaveBeenCalled();

    expect(sessionService.isLogged).not.toBeTruthy();

    sessionService.logOut();
  });
});
