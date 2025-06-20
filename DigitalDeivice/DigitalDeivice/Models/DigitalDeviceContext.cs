﻿using System;
using System.Collections.Generic;
using Microsoft.EntityFrameworkCore;

namespace DigitalDeivice.Models;

public partial class DigitalDeviceContext : DbContext
{
    public DigitalDeviceContext()
    {
    }

    public DigitalDeviceContext(DbContextOptions<DigitalDeviceContext> options)
        : base(options)
    {
    }

    public virtual DbSet<Authority> Authorities { get; set; }

    public virtual DbSet<CategoryDevice> CategoryDevices { get; set; }

    public virtual DbSet<Device> Devices { get; set; }

    public virtual DbSet<DeviceFunction> DeviceFunctions { get; set; }

    public virtual DbSet<DeviceFunctionality> DeviceFunctionalities { get; set; }

    public virtual DbSet<History> Histories { get; set; }

    public virtual DbSet<Home> Homes { get; set; }

    public virtual DbSet<HomeUser> HomeUsers { get; set; }

    public virtual DbSet<Room> Rooms { get; set; }

    public virtual DbSet<User> Users { get; set; }


    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        => optionsBuilder.UseSqlServer("Data Source=ADMIN-PC\\MSSQLSERVER1;Initial Catalog=DigitalDevice;User ID=sa;Password=Dung@123;Trust Server Certificate=True");

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
		modelBuilder.Entity<Authority>(entity =>
		{
			entity.HasKey(e => e.AuthorityId).HasName("PK__Authorit__433B1E6DF0F3C573");

			entity.ToTable("Authority");

			entity.Property(e => e.AuthorityId)
				.HasMaxLength(50)
				.IsUnicode(false)
				.HasColumnName("AuthorityID");
			entity.Property(e => e.Description).HasMaxLength(255);
			entity.Property(e => e.NameAuthority).HasMaxLength(100);
		});

		modelBuilder.Entity<CategoryDevice>(entity =>
        {
            entity.HasKey(e => e.CategoryDeviceId).HasName("PK__Category__1BBDAAC9CDA37C5E");

            entity.ToTable("CategoryDevice");

            entity.Property(e => e.CategoryDeviceId)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("CategoryDeviceID");
            entity.Property(e => e.Description).HasMaxLength(255);
            entity.Property(e => e.PhotoPath).HasMaxLength(255);
        });

        modelBuilder.Entity<Device>(entity =>
        {
            entity.HasKey(e => e.DeviceId).HasName("PK__Device__49E123312BC1E8AA");

            entity.ToTable("Device");

            entity.Property(e => e.DeviceId)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("DeviceID");
            entity.Property(e => e.CategoryDeviceId)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("CategoryDeviceID");
            entity.Property(e => e.NameDevice).HasMaxLength(100);
          
            entity.Property(e => e.PhotoPath).HasMaxLength(255);
            entity.Property(e => e.RoomId)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("RoomID");

            entity.HasOne(d => d.CategoryDevice).WithMany(p => p.Devices)
                .HasForeignKey(d => d.CategoryDeviceId)
                .HasConstraintName("FK__Device__Category__5CD6CB2B");

            entity.HasOne(d => d.Room).WithMany(p => p.Devices)
                .HasForeignKey(d => d.RoomId)
                .HasConstraintName("FK__Device__RoomID__5BE2A6F2");
        });

        modelBuilder.Entity<DeviceFunction>(entity =>
        {
            entity.HasKey(e => e.DeviceFunctionId).HasName("PK__DeviceFu__77741ED11D13AB7C");

            entity.ToTable("DeviceFunction");

            entity.Property(e => e.DeviceFunctionId)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("DeviceFunctionID");
            entity.Property(e => e.DeviceId)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("DeviceID");
            entity.Property(e => e.FunctionId)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("FunctionID");

            entity.HasOne(d => d.Device).WithMany(p => p.DeviceFunctions)
                .HasForeignKey(d => d.DeviceId)
                .HasConstraintName("FK__DeviceFun__Devic__619B8048");

            entity.HasOne(d => d.Function).WithMany(p => p.DeviceFunctions)
                .HasForeignKey(d => d.FunctionId)
                .HasConstraintName("FK__DeviceFun__Funct__628FA481");
        });

        modelBuilder.Entity<DeviceFunctionality>(entity =>
        {
            entity.HasKey(e => e.FunctionId).HasName("PK__DeviceFu__31ABF9184B23417D");

            entity.ToTable("DeviceFunctionality");

            entity.Property(e => e.FunctionId)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("FunctionID");
            entity.Property(e => e.Description).HasMaxLength(255);
        });

        modelBuilder.Entity<History>(entity =>
        {
            entity.HasKey(e => e.HistoryId).HasName("PK__History__4D7B4ADD8BDB7C89");

            entity.ToTable("History");

            entity.Property(e => e.HistoryId)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("HistoryID");
            entity.Property(e => e.Action).HasMaxLength(255);
            entity.Property(e => e.DeviceId)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("DeviceID");
            entity.Property(e => e.Time).HasColumnType("datetime");
            entity.Property(e => e.UserId)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("UserID");

            entity.HasOne(d => d.Device).WithMany(p => p.Histories)
                .HasForeignKey(d => d.DeviceId)
                .HasConstraintName("FK__History__DeviceI__66603565");

            entity.HasOne(d => d.User).WithMany(p => p.Histories)
                .HasForeignKey(d => d.UserId)
                .HasConstraintName("FK__History__UserID__656C112C");
        });

		modelBuilder.Entity<Home>(entity =>
		{
			entity.HasKey(e => e.HomeId).HasName("PK__Home__105D642256461B65");

			entity.ToTable("Home");

			entity.Property(e => e.HomeId)
				.HasMaxLength(50)
				.IsUnicode(false)
				.HasColumnName("HomeID");

			entity.Property(e => e.Name)
				.HasMaxLength(100)
				.IsRequired();

			entity.Property(e => e.Address)
				.HasMaxLength(255)
				.IsUnicode(true);

			entity.Property(e => e.PhotoPath)
				.HasMaxLength(255)
				.IsUnicode(true);

			entity.Property(e => e.UrlMqtt)
				.HasMaxLength(255)
				.IsUnicode(true)
				.HasColumnName("UrlMQTT");

			entity.Property(e => e.UserMQTT)
				.HasMaxLength(100)
				.IsUnicode(true)
				.HasColumnName("UserMQTT");

			entity.Property(e => e.PasswordMQTT)
				.HasMaxLength(100)
				.IsUnicode(true)
				.HasColumnName("PasswordMQTT");
		});


		modelBuilder.Entity<HomeUser>(entity =>
		{
			entity.HasKey(e => e.HomeUserId).HasName("PK__HomeUser__ACD15394F92E620D");

			entity.ToTable("HomeUser");

			entity.Property(e => e.HomeUserId)
				.HasMaxLength(50)
				.IsUnicode(false)
				.HasColumnName("HomeUserID");
			entity.Property(e => e.Description).HasMaxLength(255);
			entity.Property(e => e.HomeId)
				.HasMaxLength(50)
				.IsUnicode(false)
				.HasColumnName("HomeID");
			entity.Property(e => e.UserId)
				.HasMaxLength(50)
				.IsUnicode(false)
				.HasColumnName("UserID");
			entity.Property(e => e.AuthorityID)
				.HasMaxLength(50)
				.IsUnicode(false)
				.HasColumnName("AuthorityID");

			entity.HasOne(d => d.Home).WithMany(p => p.HomeUsers)
				.HasForeignKey(d => d.HomeId)
				.HasConstraintName("FK__HomeUser__HomeID__5441852A");

			entity.HasOne(d => d.Authority).WithMany(p => p.HomeUsers)
				.HasForeignKey(d => d.AuthorityID)
				.HasConstraintName("FK__HomeUser__Author__01142BA1");

			entity.HasOne(d => d.User).WithMany(p => p.HomeUsers)
				.HasForeignKey(d => d.UserId)
				.HasConstraintName("FK__HomeUser__UserID__534D60F1");
		});

		modelBuilder.Entity<Room>(entity =>
        {
            entity.HasKey(e => e.RoomId).HasName("PK__Room__32863919B15B6541");

            entity.ToTable("Room");

            entity.Property(e => e.RoomId)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("RoomID");
            entity.Property(e => e.HomeId)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("HomeID");
            entity.Property(e => e.Name).HasMaxLength(100);
            entity.Property(e => e.PhotoPath).HasMaxLength(255);

            entity.HasOne(d => d.Home).WithMany(p => p.Rooms)
                .HasForeignKey(d => d.HomeId)
                .HasConstraintName("FK__Room__HomeID__571DF1D5");
        });

        modelBuilder.Entity<User>(entity =>
        {
            entity.HasKey(e => e.UserId).HasName("PK__Users__1788CCAC6D01CC4E");

            entity.Property(e => e.UserId)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("UserID");
            entity.Property(e => e.Name).HasMaxLength(100);
            entity.Property(e => e.Password).HasMaxLength(100);
            entity.Property(e => e.Phone).HasMaxLength(20);
            entity.Property(e => e.PhotoPath).HasMaxLength(255);
        });

        OnModelCreatingPartial(modelBuilder);
    }

    partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
}
