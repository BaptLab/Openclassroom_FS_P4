import { HttpClientModule } from '@angular/common/http';
import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  flush,
  tick,
} from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';

import { LoginComponent } from './login.component';
import { Observable, of } from 'rxjs';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { LoginRequest } from '../../interfaces/loginRequest.interface';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [SessionService],
      imports: [
        RouterTestingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit credentials and navigate to the session when valid', fakeAsync(() => {
    const mockSessionInformation: SessionInformation = {
      token: 'your_mocked_token',
      type: 'your_mocked_type',
      id: 123,
      username: 'your_mocked_username',
      firstName: 'Your',
      lastName: 'Name',
      admin: true,
    };

    // Set form values
    component.form.patchValue({
      email: 'test@example.com',
      password: 'password123',
    });

    const router = TestBed.inject(Router);

    // Mock the navigate method to intercept without triggering actual navigation
    const routerNavigateSpy = jest.spyOn(router, 'navigate');

    const authServiceLoginSpy = jest
      .spyOn(component['authService'], 'login')
      .mockReturnValue(of(mockSessionInformation));
    const sessionServiceLogIn = jest.spyOn(
      component['sessionService'],
      'logIn'
    );

    component.submit();
    tick();
    fixture.detectChanges();
    flush();

    // Check if the navigate function was called with the correct parameters
    expect(routerNavigateSpy).toHaveBeenCalledWith(
      ['/sessions'],
      expect.anything()
    );

    // Ensure that navigate was called only once
    expect(routerNavigateSpy).toHaveBeenCalledTimes(1);

    // Assert other expectations
    expect(authServiceLoginSpy).toHaveBeenCalledWith({
      email: 'test@example.com',
      password: 'password123',
    });
    expect(sessionServiceLogIn).toHaveBeenCalledWith(mockSessionInformation);

    // Clear the mocks
    authServiceLoginSpy.mockRestore();
    sessionServiceLogIn.mockRestore();
    routerNavigateSpy.mockRestore();
  }));
});
