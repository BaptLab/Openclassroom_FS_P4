import { HttpClientModule } from '@angular/common/http';
import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  flush,
  tick,
} from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { of } from 'rxjs'; // Import 'of' from RxJS
import { throwError } from 'rxjs';

import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';
import { RegisterRequest } from '../../interfaces/registerRequest.interface';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  const mockRegisterValues: RegisterRequest = {
    email: 'test@gmail.com',
    firstName: 'Jean',
    lastName: 'Dupont',
    password: 'pwd',
  };

  const mockRegisterIncorrectValues: RegisterRequest = {
    email: 'test.com',
    firstName: 'Jean',
    lastName: 'Dupont',
    password: '',
  };

  beforeEach(async () => {
    const authServiceStub = {
      register: jest.fn(() => of(null)), // Mock the register method with an observable
    };

    class RouterMock {
      navigate = jest.fn();
    }

    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      imports: [
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
      ],
      providers: [
        { provide: Router, useClass: RouterMock },
        { provide: AuthService, useValue: authServiceStub }, // Provide the mocked AuthService
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;

    component.form.setValue({
      email: '',
      firstName: '',
      lastName: '',
      password: '',
    });

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should register the user', fakeAsync(() => {
    component.form.patchValue(mockRegisterValues);

    const router = TestBed.inject(Router);

    const authServiceRegisterSpy = jest.spyOn(
      component['authService'],
      'register'
    );
    const routerNavigateSpy = jest.spyOn(router, 'navigate');

    component.submit();
    tick();
    fixture.detectChanges();
    flush();

    expect(authServiceRegisterSpy).toHaveBeenCalled();
    expect(authServiceRegisterSpy).toHaveBeenCalledWith(mockRegisterValues);
    expect(routerNavigateSpy).toHaveBeenCalled();
  }));

  it('should not subscribe if credentials are invalids', fakeAsync(() => {
    component.form.patchValue(mockRegisterIncorrectValues);

    const authServiceRegisterSpy = jest
      .spyOn(component['authService'], 'register')
      .mockReturnValue(throwError(() => new Error('Invalid credentials')));

    const routerNavigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate');

    component.submit();
    tick();
    fixture.detectChanges();
    flush();

    expect(authServiceRegisterSpy).toHaveBeenCalledWith(
      mockRegisterIncorrectValues
    );
    expect(routerNavigateSpy).not.toHaveBeenCalled();
    expect(component.onError).toBeTruthy(); // Assuming onError should be set to true in case of an error
  }));
});
