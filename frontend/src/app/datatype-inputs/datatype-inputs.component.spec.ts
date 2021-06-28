import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DatatypeInputsComponent } from './datatype-inputs.component';

describe('DatatypeInputsComponent', () => {
  let component: DatatypeInputsComponent;
  let fixture: ComponentFixture<DatatypeInputsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DatatypeInputsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DatatypeInputsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
