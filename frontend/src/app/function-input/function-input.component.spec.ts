import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FunctionInputComponent } from './function-input.component';

describe('FunctionInputComponent', () => {
  let component: FunctionInputComponent;
  let fixture: ComponentFixture<FunctionInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FunctionInputComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FunctionInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
