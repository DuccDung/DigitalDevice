using System;
using System.Collections.Generic;

namespace DigitalDeivice.Models;

public partial class DeviceFunction
{
    public string DeviceFunctionId { get; set; } = null!;

    public string? DeviceId { get; set; }

    public string? FunctionId { get; set; }

    public virtual Device? Device { get; set; }

    public virtual DeviceFunctionality? Function { get; set; }
}
