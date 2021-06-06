import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomCodemirrorComponent } from './custom-codemirror.component';

describe('CustomCodemirrorComponent', () => {
  let component: CustomCodemirrorComponent;
  let fixture: ComponentFixture<CustomCodemirrorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CustomCodemirrorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomCodemirrorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
