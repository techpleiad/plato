import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddFilterRulesComponent } from './add-filter-rules.component';

describe('AddFilterRulesComponent', () => {
  let component: AddFilterRulesComponent;
  let fixture: ComponentFixture<AddFilterRulesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddFilterRulesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddFilterRulesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
